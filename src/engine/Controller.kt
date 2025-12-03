package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
@RequestMapping("/api")
class QuizEngineController @Autowired constructor(private val quizService: QuizService) {
    @GetMapping("/quiz")
    fun getQuiz(): ResponseEntity<QuizOutDto> {
        val quiz = quizService.getQuiz()

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(quiz.toDto())
    }

    @PostMapping("/quiz")
    fun answerQuiz(@RequestParam answer: Int): ResponseEntity<ResultDto> {
        val result = quizService.checkAnswer(answer = answer)

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result.toDto())
    }

    @PostMapping("/quizzes")
    fun addQuiz(@RequestBody quiz: QuizInDto): ResponseEntity<QuizOutDto> {
        val createdIdToQuiz = quizService.addQuiz(quiz.toDomain())

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(createdIdToQuiz.toDto())
    }

    @GetMapping("/quizzes/{id}")
    fun showQuiz(@PathVariable id: QuizId): ResponseEntity<QuizOutDto> {
        val quiz = quizService.getQuizWith(id)

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(quiz.toDto())
    }

    @GetMapping("/quizzes")
    fun showAllQuizzes(): ResponseEntity<List<QuizOutDto>> {
        val quizzes = quizService.getAllQuizzes()

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(quizzes.map { it.toDto() })
    }

    @PostMapping("/quizzes/{id}/solve")
    fun solveQuiz(
        @PathVariable id: QuizId,
        @RequestParam answer: Int,
    ): ResponseEntity<ResultDto> {
        val result = quizService.solveQuizWith(id, answer)

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(result.toDto())
    }
}

private fun AnswerResult.toDto() = ResultDto(
    success = success,
    feedback = feedback,
)

private fun QuizInDto.toDomain() = Quiz(
    title = title,
    text = text,
    options = options,
    answer = answer,
)

private fun Quiz.toDto() = QuizOutDto(
//    todo: Add NewQuiz domain class without ID
    id = id ?: throw IllegalArgumentException("ID of saved quiz must be not null"),
    title = title,
    text = text,
    options = options,
)

interface QuizService {
    fun getQuiz(): Quiz
    fun checkAnswer(answer: Int): AnswerResult
    fun addQuiz(quiz: Quiz): Quiz
    fun getQuizWith(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
    fun solveQuizWith(id: QuizId, answer: Int): AnswerResult
}