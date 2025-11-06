package engine

interface QuizService {
    fun getQuiz(): Pair<UInt, Quiz>
    fun checkAnswer(answerIdx: Int): AnswerResult
    fun addQuiz(quiz: Quiz): Pair<UInt, Quiz>
    fun getQuizWith(id: UInt): Pair<UInt, Quiz>
}
