package engine

interface QuizService {
    fun getQuiz(): Pair<UInt, Quiz>
    fun checkAnswer(answerIdx: Int): AnswerResult
    fun addQuiz(quiz: Quiz): Pair<UInt, Quiz>
}
