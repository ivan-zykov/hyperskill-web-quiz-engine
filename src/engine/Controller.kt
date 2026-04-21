package engine

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
@RequestMapping("/api")
class QuizEngineController @Autowired constructor(private val quizService: QuizService) {
    @GetMapping("/quiz")
    fun getInitialQuiz(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<QuizOutDto> {
        val quiz = quizService.getInitialQuiz(userDetails)

        return ResponseEntity
            .ok()
            .body(quiz.toDto())
    }

    @PostMapping("/quiz")
    fun solveInitialQuiz(
        @RequestParam answer: Int,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ResultDto> {
        val result = quizService.solveInitialQuiz(answer = answer, userDetails)

        return ResponseEntity
            .ok()
            .body(result.toDto())
    }

    @PostMapping("/quizzes")
    fun addQuiz(
        @Valid @RequestBody quiz: QuizInDto,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<QuizOutDto> {
        val createdQuiz = quizService.addQuiz(quiz.toNewQuiz(), userDetails)

        return ResponseEntity
            .ok()
            .body(createdQuiz.toDto())
    }

    @GetMapping("/quizzes/{id}")
    fun getQuizBy(@PathVariable id: QuizId): ResponseEntity<QuizOutDto> {
        val quiz = quizService.getQuizBy(id)

        return ResponseEntity
            .ok()
            .body(quiz.toDto())
    }

    @GetMapping("/quizzes")
    fun getAllQuizzes(): ResponseEntity<List<QuizOutDto>> {
        val quizzes = quizService.getAllQuizzes()

        return ResponseEntity
            .ok()
            .body(quizzes.map { it.toDto() })
    }

    @PostMapping("/quizzes/{id}/solve")
    fun solveQuizBy(
        @PathVariable id: QuizId,
        @RequestBody answer: AnswerDto,
    ): ResponseEntity<ResultDto> {
        val result = quizService.solveQuizBy(id, answer.toDomain())

        return ResponseEntity
            .ok()
            .body(result.toDto())
    }

    @PostMapping("/register")
    fun registerNewUser(@Valid @RequestBody newCredentials: UserCredentialsDTO) {
        quizService.registerNewUser(newCredentials.toDomain())
    }
}

private fun AnswerDto.toDomain(): Answer = Answer(this.answer)

private fun UserCredentialsDTO.toDomain() = UserCredentials(
    email = this.email,
    password = this.password,
)

private fun AnswerResult.toDto() = ResultDto(
    success = success,
    feedback = feedback,
)

private fun QuizInDto.toNewQuiz() = NewQuiz(
    title = title,
    text = text,
    options = options,
    answer = answer,
    authorUsername = null,
)

private fun Quiz.toDto() = QuizOutDto(
    id = id,
    title = title,
    text = text,
    options = options,
)
