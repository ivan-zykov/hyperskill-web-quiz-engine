package engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

class InMemoryQuizServiceTest {
    private val quizzesRepository = @Autowired InMemoryQuizzesRepository()
    private val sut = @Autowired QuizServiceImpl(quizzesRepository)

    @BeforeEach
    fun resetQuizzesRepository() {
        quizzesRepository.reset()
    }

    @TestFactory
    fun `Checking quiz result for`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx is ${expected.success}") {
            val actual = sut.checkAnswer(answerIdx)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Solve quiz with wrong answer`() {
        val correctAnswer = 2
        val wrongAnswer = 0
        val quiz = Quiz(
            title = "Test title",
            text = "Test text",
            options = listOf("a", "b", "c"),
            answer = correctAnswer,
        )
        val (id, _) = sut.addQuiz(quiz = quiz)

        val actual = sut.solveQuizWith(id, wrongAnswer)

        val expected = AnswerResult(success = false, feedback = WRONG_ANSWER)
        assertEquals(expected, actual)
    }

}