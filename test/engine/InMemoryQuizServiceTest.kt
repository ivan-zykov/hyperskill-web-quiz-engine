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
            val sut = InMemoryQuizService()

            val actual = sut.checkAnswer(answerIdx)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Checks wrong answer for quiz`() {
        val sut = InMemoryQuizService()
        val correctAnswer = 2
        val wrongAnswer = 0
        val quiz = Quiz(
            title = "The Java Logo",
            text = "What is depicted on the Java logo?",
            options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
            answer = correctAnswer,
        )
        sut.addQuiz(quiz = quiz)

        val actual = sut.checkAnswer(wrongAnswer)

        assertEquals(false, actual.success)
        assertEquals(WRONG_ANSWER, actual.feedback)
    }

}