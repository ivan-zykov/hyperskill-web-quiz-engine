package engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

class InMemoryQuizServiceTest {
    @TestFactory
    fun `Checking quiz result for`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx is ${expected.success}") {
//            todo: share instance of InMemoryQuizService because it is a singleton anyway and will be same instance in all tests
            val sut = InMemoryQuizService(QuizzesRepository())

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
        val sut = InMemoryQuizService(QuizzesRepository())
        val (id, _) = sut.addQuiz(quiz = quiz)

        val actual = sut.solveQuizWith(id, wrongAnswer)

        val expected = AnswerResult(success = false, feedback = WRONG_ANSWER)
        assertEquals(expected, actual)
    }

}