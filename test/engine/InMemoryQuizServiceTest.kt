package engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

class InMemoryQuizServiceTest {
    @TestFactory
    fun `Checking quiz result for`() = listOf(
        2 to AnswerResult(success = true, feedback = CONGRATULATIONS),
        1 to AnswerResult(success = false, feedback = WRONG_ANSWER),
    ).map { (answerIdx, expected) ->
        dynamicTest("answer $answerIdx is ${expected.success}") {
            val sut = InMemoryQuizService()

            val actual = sut.checkAnswer(answerIdx)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Getting quiz with non-existent id throws exception`() {
        val sut = InMemoryQuizService()
        val quzId = 0U

        assertThrows<IllegalStateException>("Error. Failed to get quiz with id $quzId") {
            sut.getQuizWith(quzId)
        }
    }
}