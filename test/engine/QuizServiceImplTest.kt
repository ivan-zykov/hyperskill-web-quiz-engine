package engine

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@DataJpaTest
@ActiveProfiles("test")
class QuizServiceImplTest @Autowired constructor(private val userRepo: AppUserRepository) {
    private lateinit var sut: QuizServiceImpl

    private val user = AppUser(
        id = 1,
        username = "test@user.com",
        password = "testPass"
    )
    private val userDetails: UserDetails = AppUserAdapter(user)

    @BeforeEach
    fun setUp() {
        val quizzesRepository = InMemoryQuizzesRepository()
        sut = QuizServiceImpl(
            quizzesRepository,
            userRepo,
            BCryptPasswordEncoder(7)
        )
    }

    private val quiz1Id = QuizId(1)
    private val newQuiz1 = NewQuiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
        answer = listOf(2),
        authorUsername = null,
    )
    private val userCredentials = UserCredentials(
        email = "vanya@mail.com",
        password = "12345"
    )

    @Test
    fun `Gets initial quiz`() {
        val actual = sut.getInitialQuiz(userDetails)

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) },
            { assertEquals(user.username, actual.authorUsername) }
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
        val actual = sut.addQuiz(newQuiz = newQuiz1, userDetails = userDetails)

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) },
            { assertEquals(user.username, actual.authorUsername) }
        )
    }

    @Test
    fun `Gets quiz by ID`() {
        sut.addQuiz(newQuiz1, userDetails)

        val actual = sut.getQuizBy(id = quiz1Id)

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) },
            { assertEquals(user.username, actual.authorUsername) }
        )
    }

    @Test
    fun `Gets zero quizzes`() {
        val actual = sut.getAllQuizzes()

        assertEquals(listOf<Quiz>(), actual)
    }

    @Test
    fun `Gets two quizzes`() {
        sut.addQuiz(newQuiz = newQuiz1, userDetails = userDetails)
        val newQuiz2 = newQuiz1.copy(title = "The Java Logo 2")
        val quiz2Id = QuizId(2)
        sut.addQuiz(newQuiz = newQuiz2, userDetails = userDetails)

        val actual = sut.getAllQuizzes()

        assertAll(
            { assertEquals(2, actual.size) },
            { assertEquals(quiz1Id, actual[0].id) },
            { assertEquals(newQuiz1.title, actual[0].title) },
            { assertEquals(newQuiz1.text, actual[0].text) },
            { assertEquals(newQuiz1.options, actual[0].options) },
            { assertEquals(newQuiz1.answer, actual[0].answer) },
            { assertEquals(user.username, actual[0].authorUsername) },
            { assertEquals(quiz2Id, actual[1].id) },
            { assertEquals(newQuiz2.title, actual[1].title) },
            { assertEquals(newQuiz2.text, actual[1].text) },
            { assertEquals(newQuiz2.options, actual[1].options) },
            { assertEquals(newQuiz2.answer, actual[1].answer) },
            { assertEquals(user.username, actual[1].authorUsername) },
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
}
