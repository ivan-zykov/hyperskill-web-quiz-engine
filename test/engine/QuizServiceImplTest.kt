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

    private val quiz1Id = 331833382
    private val quiz1 = Quiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
        answer = 2,
    )

    @BeforeEach
    fun resetQuizzesRepository() {
        quizzesRepository.reset()
    }

    @Test
    fun `Returns initial quiz`() {
        val actual = sut.getQuiz()

        assertAll(
            { assertEquals(quiz1Id, actual.first) },
            { assertEquals(quiz1.title, actual.second.title) },
            { assertEquals(quiz1.text, actual.second.text) },
            { assertEquals(quiz1.options, actual.second.options) },
            { assertEquals(quiz1.answer, actual.second.answer) }
        )
    }

    @TestFactory
    fun `Checking quiz result for`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx is ${expected.success}") {
            val actual = sut.checkAnswer(answer = answerIdx)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Adds a quiz`() {
        val actual = sut.addQuiz(quiz = quiz1)

        assertAll(
            { assertEquals(quiz1Id, actual.first) },
            { assertEquals(quiz1.title, actual.second.title) },
            { assertEquals(quiz1.text, actual.second.text) },
            { assertEquals(quiz1.options, actual.second.options) },
            { assertEquals(quiz1.answer, actual.second.answer) }
        )
    }

    @Test
    fun `Gets quiz with ID`() {
        sut.addQuiz(quiz1)

        val actual = sut.getQuizWith(id = quiz1Id)

        assertAll(
            { assertEquals(quiz1Id, actual.first) },
            { assertEquals(quiz1.title, actual.second.title) },
            { assertEquals(quiz1.text, actual.second.text) },
            { assertEquals(quiz1.options, actual.second.options) },
            { assertEquals(quiz1.answer, actual.second.answer) }
        )
    }

    @Test
    fun `Gets zero quizzes`() {
        val actual = sut.getAllQuizzes()

        assertEquals(listOf<QuizWithId>(), actual)
    }

    @Test
    fun `Gets two quizzes`() {
        sut.addQuiz(quiz = quiz1)
        val quiz2 = quiz1.copy(title = "The Java Logo 2")
        val quiz2Id = 1064299156
        sut.addQuiz(quiz = quiz2)

        val actual = sut.getAllQuizzes()

        assertAll(
            { assertEquals(2, actual.size) },
            { assertEquals(quiz1Id, actual[0].first) },
            { assertEquals(quiz1.title, actual[0].second.title) },
            { assertEquals(quiz1.text, actual[0].second.text) },
            { assertEquals(quiz1.options, actual[0].second.options) },
            { assertEquals(quiz1.answer, actual[0].second.answer) },
            { assertEquals(quiz2Id, actual[1].first) },
            { assertEquals(quiz2.title, actual[1].second.title) },
            { assertEquals(quiz2.text, actual[1].second.text) },
            { assertEquals(quiz2.options, actual[1].second.options) },
            { assertEquals(quiz2.answer, actual[1].second.answer) }
        )
    }

    @TestFactory
    fun `Solve quiz with`() = listOf(
        Triple(
            "correct answer",
            2,
            AnswerResult(success = true, feedback = CONGRATULATIONS)
        ),
        Triple(
            "wrong answer",
            0,
            AnswerResult(success = false, feedback = WRONG_ANSWER)
        )
    ).map { (displayName, answer, expected) ->
        dynamicTest(displayName) {
            val (id, _) = sut.addQuiz(quiz = quiz1)

            val actual = sut.solveQuizWith(id = id, answer = answer)

            assertEquals(expected, actual)
        }
    }
}