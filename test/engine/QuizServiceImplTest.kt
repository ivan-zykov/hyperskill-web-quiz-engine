package engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

class QuizServiceImplTest {
    private val quizzesRepository = @Autowired InMemoryQuizzesRepository()
    private val sut = @Autowired QuizServiceImpl(quizzesRepository)

    private val quiz1Id = QuizId(1)
    private val newQuiz1 = NewQuiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
        answer = listOf(2),
    )

    @BeforeEach
    fun resetQuizzesRepository() {
        quizzesRepository.reset()
    }

    @Test
    fun `Returns initial quiz`() {
        val actual = sut.getInitialQuiz()

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) }
        )
    }

    @TestFactory
    fun `Checking quiz result for initial quiz with`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx is ${expected.success}") {
            val actual = sut.solveInitialQuiz(answer = answerIdx)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Adds a quiz`() {
        val actual = sut.addQuiz(newQuiz = newQuiz1)

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) }
        )
    }

    @Test
    fun `Gets quiz with ID`() {
        sut.addQuiz(newQuiz1)

        val actual = sut.getQuizBy(id = quiz1Id)

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) }
        )
    }

    @Test
    fun `Gets zero quizzes`() {
        val actual = sut.getAllQuizzes()

        assertEquals(listOf<Quiz>(), actual)
    }

    @Test
    fun `Gets two quizzes`() {
        sut.addQuiz(newQuiz = newQuiz1)
        val newQuiz2 = newQuiz1.copy(title = "The Java Logo 2")
        val quiz2Id = QuizId(2)
        sut.addQuiz(newQuiz = newQuiz2)

        val actual = sut.getAllQuizzes()

        assertAll(
            { assertEquals(2, actual.size) },
            { assertEquals(quiz1Id, actual[0].id) },
            { assertEquals(newQuiz1.title, actual[0].title) },
            { assertEquals(newQuiz1.text, actual[0].text) },
            { assertEquals(newQuiz1.options, actual[0].options) },
            { assertEquals(newQuiz1.answer, actual[0].answer) },
            { assertEquals(quiz2Id, actual[1].id) },
            { assertEquals(newQuiz2.title, actual[1].title) },
            { assertEquals(newQuiz2.text, actual[1].text) },
            { assertEquals(newQuiz2.options, actual[1].options) },
            { assertEquals(newQuiz2.answer, actual[1].answer) }
        )
    }

    @TestFactory
    fun `Solve quiz by ID with`() = listOf(
        Triple(
            "correct answer",
            listOf(2),
            AnswerResult(success = true, feedback = CONGRATULATIONS)
        ),
        Triple(
            "wrong answer",
            listOf(0),
            AnswerResult(success = false, feedback = WRONG_ANSWER)
        )
    ).map { (displayName, answer, expected) ->
        dynamicTest(displayName) {
            val addedQuizId = sut.addQuiz(newQuiz = newQuiz1).id

            val actual = sut.solveQuizBy(id = addedQuizId, answer = answer)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Solve quiz with answers = null and empty provided answer`() {
        val quizWithNullAnswer = newQuiz1.copy(answer = null)
        val addedQuizId = sut.addQuiz(newQuiz = quizWithNullAnswer).id

        val actual = sut.solveQuizBy(id = addedQuizId, answer = listOf())

        assertEquals(true, actual.success)
        assertEquals(CONGRATULATIONS, actual.feedback)
    }
}