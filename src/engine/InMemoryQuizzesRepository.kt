package engine

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class InMemoryQuizzesRepository : QuizzesRepository {
    private val quizzes: ConcurrentMap<Int, Quiz> = ConcurrentHashMap()

    override fun addQuiz(quiz: Quiz): QuizWithId {
        val newId = quiz.generateId()
        save(newId, quiz)

        return findQuizWith(newId)
    }

    override fun findQuizWith(id: Int): QuizWithId {
        val quiz = quizzes[id]
            ?: throw QuizNotFoundException("Error. There is no quiz with id $id.")

        return id to quiz
    }

    override fun getAllQuizzes(): List<QuizWithId> = quizzes.toList()

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