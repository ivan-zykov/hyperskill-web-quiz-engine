package engine

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Suppress("unused")
@Service
class InMemoryQuizService : QuizService {
    override fun getQuiz(): QuizWithId = 0U to Quiz(
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

    override fun addQuiz(quiz: Quiz): QuizWithId {
        val newId = quiz.generateId()
        quizzes.add(newId, quiz)

        val createdQuiz = findQuizWith(newId)
        checkNotNull(createdQuiz) { "Error. Failed to persist new quiz $quiz with id $newId." }

        return newId to createdQuiz
    }

    override fun getQuizWith(id: UInt): QuizWithId {
        val quiz = findQuizWith(id) ?: throw QuizNotFoundException("Error. There is no quiz with id $id.")

        return id to quiz
    }

    override fun getAllQuizzes(): List<QuizWithId> = quizzes.toList()

    fun clearQuizzes() = quizzes.clear()

    private fun findQuizWith(id: UInt) = quizzes[id]

    private fun Quiz.generateId() = title.hashCode().toUInt()

}

private fun ConcurrentMap<UInt, Quiz>.add(
    id: UInt,
    quiz: Quiz
) {
    this[id] = quiz
}

class QuizNotFoundException(message: String) : RuntimeException(message)
