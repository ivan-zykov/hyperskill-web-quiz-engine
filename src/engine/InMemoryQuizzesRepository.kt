package engine

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class InMemoryQuizzesRepository : QuizzesRepository {
    private val quizzes: ConcurrentMap<QuizId, Quiz> = ConcurrentHashMap()

    override fun addQuiz(quiz: Quiz): Quiz {
        val newId = quiz.generateId()
        save(newId, quiz)

        return findQuizBy(newId)
    }

    override fun findQuizBy(id: QuizId): Quiz = quizzes[id]
        ?: throw QuizNotFoundException("Error. There is no quiz with id $id.")

    override fun getAllQuizzes(): List<Quiz> = quizzes.values.toList()

    fun reset() {
        quizzes.clear()
    }

    private fun save(
        newId: QuizId,
        quiz: Quiz
    ) {
        val quizWithId = quiz.copy(id = newId)
        quizzes[newId] = quizWithId
    }

}

// todo: why this instead of just counter variable?
private fun Quiz.generateId(): QuizId {
    val hashCode = this.title.hashCode()
    val value = if (hashCode >= 0) hashCode else hashCode.unaryMinus()
    return QuizId(value)
}

class QuizNotFoundException(message: String) : RuntimeException(message)