package engine

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
@RequestMapping("/api")
class QuizEngineController @Autowired constructor(private val quizService: QuizService) {
    @GetMapping("/quiz")
    fun getInitialQuiz(): ResponseEntity<QuizOutDto> {
        val quiz = quizService.getInitialQuiz()

        return ResponseEntity
            .ok()
            .body(quiz.toDto())
    }

    @PostMapping("/quiz")
    fun solveInitialQuiz(@RequestParam answer: Int): ResponseEntity<ResultDto> {
        val result = quizService.solveInitialQuiz(answer = answer)

        return ResponseEntity
            .ok()
            .body(result.toDto())
    }

    @PostMapping("/quizzes")
    fun addQuiz(@Valid @RequestBody quiz: QuizInDto): ResponseEntity<QuizOutDto> {
        val createdQuiz = quizService.addQuiz(quiz.toNewQuiz())

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
        @RequestParam answer: List<Int>,
    ): ResponseEntity<ResultDto> {
        val result = quizService.solveQuizBy(id, answer)

        return ResponseEntity
            .ok()
            .body(result.toDto())
    }
}

private fun AnswerResult.toDto() = ResultDto(
    success = success,
    feedback = feedback,
)

private fun QuizInDto.toNewQuiz() = NewQuiz(
    title = title,
    text = text,
    options = options,
    answer = answer,
)

private fun Quiz.toDto() = QuizOutDto(
    id = id,
    title = title,
    text = text,
    options = options,
)

interface QuizService {
    fun getInitialQuiz(): Quiz
    fun solveInitialQuiz(answer: Int): AnswerResult
    fun addQuiz(newQuiz: NewQuiz): Quiz
    fun getQuizBy(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
    fun solveQuizBy(id: QuizId, answer: List<Int>): AnswerResult
}