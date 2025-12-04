package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Service
class QuizServiceImpl @Autowired constructor(private val quizzesRepo: QuizzesRepository) : QuizService {
    override fun getQuiz(): Quiz = addInitialQuiz()

    override fun checkAnswer(answer: Int): AnswerResult {
        val initialQuiz = addInitialQuiz()

        val (success, feedback) = initialQuiz.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    override fun addQuiz(newQuiz: NewQuiz): Quiz = quizzesRepo.addQuiz(newQuiz)

    override fun getQuizWith(id: QuizId): Quiz = quizzesRepo.findQuizBy(id)

    override fun getAllQuizzes(): List<Quiz> = quizzesRepo.getAllQuizzes()

    override fun solveQuizWith(
        id: QuizId,
        answer: Int
    ): AnswerResult {
        val quiz = getQuizWith(id)

        val (success, feedback) = quiz.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    private fun addInitialQuiz() = addQuiz(
        NewQuiz(
            title = "The Java Logo",
            text = "What is depicted on the Java logo?",
            options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
            answer = 2,
        )
    )

}

interface QuizzesRepository {
    fun addQuiz(newQuiz: NewQuiz): Quiz
    fun findQuizBy(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
}

private fun Quiz.check(answer: Int) =
    if (this.answer == answer) {
        true to CONGRATULATIONS
    } else {
        false to WRONG_ANSWER
    }
