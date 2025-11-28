package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Service
class InMemoryQuizService @Autowired constructor(private val quizzesRepo: QuizzesRepository) : QuizService {
    override fun getQuiz(): QuizWithId = addInitialQuiz()

    override fun checkAnswer(answer: Int): AnswerResult {
        val initialQuiz = addInitialQuiz()

        val (success, feedback) = initialQuiz.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    override fun addQuiz(quiz: Quiz): QuizWithId {
        val newId = quiz.generateId()
        quizzesRepo.addQuiz(newId, quiz)

        val createdQuiz = quizzesRepo.findQuizWith(newId)
        checkNotNull(createdQuiz) { "Error. Failed to persist new quiz $quiz with id $newId." }

        return newId to createdQuiz
    }

    override fun getQuizWith(id: Int): QuizWithId {
        val quiz = quizzesRepo.findQuizWith(id)
        return id to quiz
    }

    override fun getAllQuizzes(): List<QuizWithId> = quizzesRepo.getAllQuizzes()

    override fun solveQuizWith(
        id: Int,
        answer: Int
    ): AnswerResult {
        val quizWithId = getQuizWith(id)

        val (success, feedback) = quizWithId.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    private fun addInitialQuiz() = addQuiz(
        Quiz(
            title = "The Java Logo",
            text = "What is depicted on the Java logo?",
            options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
            answer = 2,
        )
    )

    private fun QuizWithId.check(answer: Int) =
        if (second.answer == answer) {
            true to CONGRATULATIONS
        } else {
            false to WRONG_ANSWER
        }

    private fun Quiz.generateId(): Int {
        val hashCode = title.hashCode()
        return if (hashCode >= 0) hashCode else hashCode.unaryMinus()
    }

}
