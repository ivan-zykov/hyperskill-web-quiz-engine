package engine

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

private const val USERNAME = "test@user.com"
private const val PASSWORD = "testPass"

@DataJpaTest
@Import(PasswordEncoderConfig::class)
@ActiveProfiles("test")
class QuizServiceTest @Autowired constructor(
    private val userRepo: AppUserRepository,
    quizRepo: CrudQuizzesRepository,
    passEncoder: PasswordEncoder,
) {
    private val sut = QuizService(
        userRepo,
        quizRepo,
        passEncoder
    )

    private val user = AppUser(
        id = 1,
        username = USERNAME,
        password = PASSWORD
    )
    val otherUser = AppUser(
        username = "other@user.com",
        password = PASSWORD
    )
    private val encodedUser = AppUser(
        username = USERNAME,
        password = passEncoder.encode(PASSWORD)
    )
    private val userDetails: UserDetails = AppUserAdapter(user)

    @BeforeEach
    fun setUp() {
        userRepo.save(encodedUser)
    }

    private val newQuiz1 = NewQuiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
        answer = listOf(2),
    )
    private val userCredentials = UserCredentials(
        email = "vanya@mail.com",
        password = "12345"
    )

    @Test
    fun `Gets initial quiz`() {
        val actualQuiz = sut.getInitialQuiz(userDetails)

        assertAll(
            { assertEquals(newQuiz1.title, actualQuiz.title) },
            { assertEquals(newQuiz1.text, actualQuiz.text) },
            { assertEquals(newQuiz1.options, actualQuiz.options) },
            { assertEquals(newQuiz1.answer, actualQuiz.answer) },
            { assertEquals(user.username, actualQuiz.authorUsername) }
        )
    }

    @TestFactory
    fun `Solves initial quiz with`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx is ${expected.success}") {
            val actual = sut.solveInitialQuiz(answer = answerIdx, userDetails = userDetails)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Adds a quiz`() {
        val actualQuiz = sut.addQuiz(newQuiz = newQuiz1, userDetails = userDetails)

        assertAll(
            { assertEquals(newQuiz1.title, actualQuiz.title) },
            { assertEquals(newQuiz1.text, actualQuiz.text) },
            { assertEquals(newQuiz1.options, actualQuiz.options) },
            { assertEquals(newQuiz1.answer, actualQuiz.answer) },
            { assertEquals(user.username, actualQuiz.authorUsername) }
        )
    }

    @Test
    fun `Adding quiz throws when user not found`() {
        val otherUserDetails = AppUserAdapter(otherUser)

        val exception = assertThrows<RuntimeException> {
            sut.addQuiz(newQuiz = newQuiz1, userDetails = otherUserDetails)
        }
        assertEquals(
            "Server error. User ${otherUserDetails.username} was not found.",
            exception.message
        )
    }

    @Test
    fun `Gets quiz by ID`() {
        val savedQuiz = sut.addQuiz(newQuiz1, userDetails)

        val fetchedQuiz = sut.getQuizBy(id = savedQuiz.id)

        assertAll(
            { assertEquals(savedQuiz.id, fetchedQuiz.id) },
            { assertEquals(savedQuiz.title, fetchedQuiz.title) },
            { assertEquals(savedQuiz.text, fetchedQuiz.text) },
            { assertEquals(savedQuiz.options, fetchedQuiz.options) },
            { assertEquals(savedQuiz.answer, fetchedQuiz.answer) },
            { assertEquals(user.username, fetchedQuiz.authorUsername) }
        )
    }

    @Test
    fun `Gets zero quizzes`() {
        val actual = sut.getAllQuizzes()

        assertEquals(listOf<Quiz>(), actual)
    }

    @Test
    fun `Gets two quizzes`() {
        val savedQuiz1 = sut.addQuiz(newQuiz = newQuiz1, userDetails = userDetails)
        val newQuiz2 = newQuiz1.copy(title = "The Java Logo 2")
        val savedQuiz2 = sut.addQuiz(newQuiz = newQuiz2, userDetails = userDetails)

        val fetchedQuizzes = sut.getAllQuizzes()

        assertAll(
            { assertEquals(2, fetchedQuizzes.size) },
            { assertEquals(savedQuiz1.id, fetchedQuizzes[0].id) },
            { assertEquals(savedQuiz1.title, fetchedQuizzes[0].title) },
            { assertEquals(savedQuiz1.text, fetchedQuizzes[0].text) },
            { assertEquals(savedQuiz1.options, fetchedQuizzes[0].options) },
            { assertEquals(savedQuiz1.answer, fetchedQuizzes[0].answer) },
            { assertEquals(user.username, fetchedQuizzes[0].authorUsername) },
            { assertEquals(savedQuiz2.id, fetchedQuizzes[1].id) },
            { assertEquals(savedQuiz2.title, fetchedQuizzes[1].title) },
            { assertEquals(savedQuiz2.text, fetchedQuizzes[1].text) },
            { assertEquals(savedQuiz2.options, fetchedQuizzes[1].options) },
            { assertEquals(savedQuiz2.answer, fetchedQuizzes[1].answer) },
            { assertEquals(user.username, fetchedQuizzes[1].authorUsername) },
        )
    }

    @TestFactory
    fun `Solves quiz by ID with`() = listOf(
        Triple(
            "correct answer",
            Answer(listOf(2)),
            AnswerResult(success = true, feedback = CONGRATULATIONS)
        ),
        Triple(
            "wrong answer",
            Answer(listOf(0, 1)),
            AnswerResult(success = false, feedback = WRONG_ANSWER)
        )
    ).map { (displayName, answer, expected) ->
        dynamicTest(displayName) {
            val addedQuizId = sut.addQuiz(newQuiz = newQuiz1, userDetails = userDetails).id

            val actual = sut.solveQuizBy(id = addedQuizId, answer = answer)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Solves quiz with answers = null and empty provided answer`() {
        val quizWithNullAnswer = newQuiz1.copy(answer = null)
        val addedQuizId = sut.addQuiz(newQuiz = quizWithNullAnswer, userDetails = userDetails).id

        val actual = sut.solveQuizBy(id = addedQuizId, answer = Answer(listOf()))

        assertEquals(true, actual.success)
        assertEquals(CONGRATULATIONS, actual.feedback)
    }

    @Test
    fun `Registers new user`() {
        assertDoesNotThrow {
            sut.registerNewUser(userCredentials)
        }
    }

    @Test
    fun `Registering duplicate new user throws`() {
        sut.registerNewUser(userCredentials)

        val exception = assertThrows<DuplicatedUserException> {
            sut.registerNewUser(userCredentials)
        }
        assertEquals("User with email ${userCredentials.email} already exists", exception.message)
    }

    @Test
    fun `Deletes quiz of the same author`() {
        sut.addQuiz(newQuiz1, userDetails)
        val quiz = sut.getAllQuizzes().first()

        sut.deleteQuizBy(quiz.id, userDetails)

        assertTrue { sut.getAllQuizzes().isEmpty() }
    }

    @Test
    fun `Deleting quiz of different author throws`() {
        sut.addQuiz(newQuiz1, userDetails)
        val quiz = sut.getAllQuizzes().first()
        val otherUserDetails = AppUserAdapter(otherUser)

        val exception = assertThrows<AccessDeniedException> {
            sut.deleteQuizBy(quiz.id, otherUserDetails)
        }
        assertEquals(
            "Error. Username ${otherUser.username} doesn't math the author's username of quiz with ID ${quiz.id.value}.",
            exception.message
        )
    }
}
