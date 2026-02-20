package engine

interface QuizService {
    fun getInitialQuiz(): Quiz
    fun solveInitialQuiz(answer: Int): AnswerResult
    fun addQuiz(newQuiz: NewQuiz): Quiz
    fun getQuizBy(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
    fun solveQuizBy(id: QuizId, answer: Answer): AnswerResult
}