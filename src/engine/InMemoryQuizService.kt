package engine

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Service
class InMemoryQuizService : QuizService {
    private val quizzes: ConcurrentMap<UInt, Quiz> = ConcurrentHashMap()

    override fun getQuiz(): QuizWithId = addInitialQuiz()

    override fun checkAnswer(answer: Int): AnswerResult {
        val initialQuiz = addInitialQuiz()

        val (success, feedback) = initialQuiz.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

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

    override fun solveQuizWith(id: UInt, answer: Int): AnswerResult {
        val quizWithId = getQuizWith(id)

        val (success, feedback) = quizWithId.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    fun reset() = quizzes.clear()

    private fun addInitialQuiz() = addQuiz(
        Quiz(
            title = "The Java Logo",
            text = "What is depicted on the Java logo?",
            options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
            answer = 2,
        )
    )

    private fun QuizWithId.check(answer: Int) =
        if (second.answer == answer) {
            true to CONGRATULATIONS
        } else {
            false to WRONG_ANSWER
        }

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
