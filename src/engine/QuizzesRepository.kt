package engine

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class QuizzesRepository {
    private val quizzes: ConcurrentMap<Int, Quiz> = ConcurrentHashMap()

    fun addQuiz(id: Int, quiz: Quiz) {
        quizzes[id] = quiz
    }

    fun findQuizWith(id: Int) = quizzes[id]
        ?: throw QuizNotFoundException("Error. There is no quiz with id $id.")

    fun getAllQuizzes(): List<QuizWithId> = quizzes.toList()

    fun reset() {
        quizzes.clear()
    }

}

class QuizNotFoundException(message: String) : RuntimeException(message)