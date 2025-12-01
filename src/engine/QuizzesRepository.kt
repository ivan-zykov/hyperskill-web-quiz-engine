package engine

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class QuizzesRepository {
    private val quizzes: ConcurrentMap<Int, Quiz> = ConcurrentHashMap()

    fun addQuiz(quiz: Quiz): QuizWithId {
        val newId = quiz.generateId()
        save(newId, quiz)
        val persistedQuiz = findQuizWith(newId)

        return newId to persistedQuiz
    }

    fun findQuizWith(id: Int) = quizzes[id]
        ?: throw QuizNotFoundException("Error. There is no quiz with id $id.")

    fun getAllQuizzes(): List<QuizWithId> = quizzes.toList()

    fun reset() {
        quizzes.clear()
    }

    private fun save(
        newId: Int,
        quiz: Quiz
    ) {
        quizzes[newId] = quiz
    }

}

private fun Quiz.generateId(): Int {
    val hashCode = title.hashCode()
    return if (hashCode >= 0) hashCode else hashCode.unaryMinus()
}

class QuizNotFoundException(message: String) : RuntimeException(message)