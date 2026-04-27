package engine

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val CONGRATULATIONS = "Congratulations, you're right!"
private const val WRONG_ANSWER = "Wrong answer! Please, try again."

@Service
class QuizService @Autowired constructor(
    private val quizzesRepo: JpaQuizzesRepositoryAdapter,
    private val userRepository: AppUserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    private val logger: Logger = LoggerFactory.getLogger(QuizService::class.java)

    fun getInitialQuiz(userDetails: UserDetails): Quiz = addInitialQuiz(userDetails)

    fun solveInitialQuiz(answer: Int, userDetails: UserDetails): AnswerResult {
        val initialQuiz = addInitialQuiz(userDetails)

        val answerWrapped = Answer(listOf(answer))
        val (success, feedback) = initialQuiz.check(answerWrapped)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    fun addQuiz(newQuiz: NewQuiz, userDetails: UserDetails): Quiz {
        newQuiz.authorUsername = userDetails.username
        return quizzesRepo.addQuiz(newQuiz)
    }

    fun getQuizBy(id: QuizId): Quiz =
        logExecutionTimeWithMessage("Getting a quiz with ID ${id.value} took") { quizzesRepo.findQuizBy(id) }

    fun getAllQuizzes(): List<Quiz> = quizzesRepo.getAllQuizzes()

    fun solveQuizBy(
        id: QuizId,
        answer: Answer
    ): AnswerResult {
        val quiz = getQuizBy(id)

        val (success, feedback) = quiz.check(answer)

        return AnswerResult(
            success = success,
            feedback = feedback,
        )
    }

    fun registerNewUser(credentials: UserCredentials) {
        val existingUser = userRepository.findByUsername(credentials.email)
        if (existingUser != null) {
            throw DuplicatedUserException("User with email ${credentials.email} already exists")
        }
        val newUser = AppUser(
            username = credentials.email,
            password = passwordEncoder.encode(credentials.password)
        )
        userRepository.save(newUser)
    }

    fun deleteQuizBy(
        id: QuizId,
        userDetails: UserDetails
    ) {
        val quiz = quizzesRepo.findQuizBy(id)

        if (userDetails.username.equals(quiz.authorUsername).not()) {
            throw AccessDeniedException(
                "Error. Username ${userDetails.username} doesn't math the author's username of quiz with ID ${id.value}."
            )
        }

        quizzesRepo.deleteById(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun <T> logExecutionTimeWithMessage(
        actionDescription: String,
        block: () -> T,
    ): T {
        val start = Clock.System.now()
        val result = block.invoke()
        val duration = (Clock.System.now() - start).inWholeMilliseconds
        logger.info("$actionDescription $duration millisecond(s)")

        return result
    }

    private fun addInitialQuiz(userDetails: UserDetails) = addQuiz(
        NewQuiz(
            title = "The Java Logo",
            text = "What is depicted on the Java logo?",
            options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
            answer = listOf(2),
        ),
        userDetails
    )
}

private fun Quiz.check(answer: Answer) =
    if (this.answer?.toSet() == answer.value.toSet() ||
        (this.answer == null && answer.value.isEmpty())
    ) {
        true to CONGRATULATIONS
    } else {
        false to WRONG_ANSWER
    }
