package engine

interface QuizzesRepository {
    fun addQuiz(newQuiz: NewQuiz): Quiz
    fun findQuizBy(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
    fun reset()
}