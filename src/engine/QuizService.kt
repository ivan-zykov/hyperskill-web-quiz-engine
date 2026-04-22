package engine

import org.springframework.security.core.userdetails.UserDetails

interface QuizService {
    fun getInitialQuiz(userDetails: UserDetails): Quiz
    fun solveInitialQuiz(answer: Int, userDetails: UserDetails): AnswerResult
    fun addQuiz(newQuiz: NewQuiz, userDetails: UserDetails): Quiz
    fun getQuizBy(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
    fun solveQuizBy(id: QuizId, answer: Answer): AnswerResult
    fun registerNewUser(credentials: UserCredentials)
    fun deleteQuizBy(id: QuizId, userDetails: UserDetails)
}
