package engine

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Suppress("unused")
@Service
class InMemoryQuizService : QuizService {
    override fun getQuiz(): Pair<UInt, Quiz> = 0U to Quiz(
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

    private val quizzes: ConcurrentMap<UInt, Quiz> = ConcurrentHashMap()

    override fun addQuiz(quiz: Quiz): Pair<UInt, Quiz> {
        val newId = quiz.generateId()
        quizzes.add(newId, quiz)

        val createdQuiz = getQuizWith(newId)
        checkNotNull(createdQuiz) { "Error. Failed to persist new quiz $quiz with id $newId." }

        return newId to createdQuiz
    }

    private fun getQuizWith(newId: UInt) = quizzes[newId]

    private fun Quiz.generateId() = title.hashCode().toUInt()

}

private fun ConcurrentMap<UInt, Quiz>.add(
    id: UInt,
    quiz: Quiz
) {
    this[id] = quiz
}
