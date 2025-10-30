package engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

class InMemoryQuizServiceTest {
    @TestFactory
    fun `Result for`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx") {
            val sut = InMemoryQuizService()

            val actual = sut.checkAnswer(answerIdx)

            assertEquals(expected, actual)
        }
    }
}