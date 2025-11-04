package engine

interface QuizService {
    fun getQuiz(): Pair<Int, Quiz>
    fun checkAnswer(answerIdx: Int): AnswerResult
    fun addQuiz(quiz: Quiz): Pair<Int, Quiz>
}
