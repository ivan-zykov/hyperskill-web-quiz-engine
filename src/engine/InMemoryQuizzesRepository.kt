package engine

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class InMemoryQuizzesRepository : QuizzesRepository {
    private val quizzes: ConcurrentMap<QuizId, Quiz> = ConcurrentHashMap()
    private var nextQuizIdValue = 1

    override fun addQuiz(quiz: Quiz): Quiz {
        val newId = generateNextId()
        save(newId, quiz)

        return findQuizBy(newId)
    }

    private fun generateNextId(): QuizId = QuizId(nextQuizIdValue++)

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

class QuizNotFoundException(message: String) : RuntimeException(message)