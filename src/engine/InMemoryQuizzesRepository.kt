package engine

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class InMemoryQuizzesRepository : QuizzesRepository {
    private val quizzes: ConcurrentMap<QuizId, Quiz> = ConcurrentHashMap()
    private var nextQuizIdValue = 1

    override fun addQuiz(newQuiz: NewQuiz): Quiz {
        val newId = generateNextId()
        save(newId, newQuiz)

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
        newQuiz: NewQuiz
    ) {
        val quiz = Quiz(
            title = newQuiz.title,
            text = newQuiz.text,
            options = newQuiz.options,
            answer = newQuiz.answer,
            id = newId,
        )
        quizzes[newId] = quiz
    }

}

class QuizNotFoundException(message: String) : RuntimeException(message)