package engine

import org.springframework.stereotype.Service

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Suppress("unused")
@Service
class InMemoryQuizService : QuizService {
    override fun getQuiz(): Pair<Int, Quiz> = 0 to Quiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
        answer = 2,
    )

    override fun checkAnswer(answerIdx: Int): AnswerResult {
        val (success, feedback) = if (answerIdx == 2) {
            true to CONGRATULATIONS
        } else {
            false to WRONG_ANSWER
        }

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

}
