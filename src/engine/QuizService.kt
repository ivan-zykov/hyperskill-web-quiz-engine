package engine

interface QuizService {
    fun getQuiz(): Quiz
    fun checkAnswer(answerIdx: Int): AnswerResult
}
