package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

typealias QuizWithId = Pair<UInt, Quiz>

@Suppress("unused")
@RestController
@RequestMapping("/api")
class QuizEngineController @Autowired constructor(val quizService: QuizService) {
    @GetMapping("/quiz")
    fun getQuiz(): ResponseEntity<QuizOutDto> {
        val idToQuiz = quizService.getQuiz()

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(idToQuiz.toDto())
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
    fun showQuiz(@PathVariable id: UInt): ResponseEntity<QuizOutDto> {
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
        @PathVariable id: UInt,
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

private fun Pair<UInt, Quiz>.toDto() = QuizOutDto(
    id = first,
    title = second.title,
    text = second.text,
    options = second.options,
)

interface QuizService {
    fun getQuiz(): QuizWithId
    fun checkAnswer(answer: Int): AnswerResult
    fun addQuiz(quiz: Quiz): QuizWithId
    fun getQuizWith(id: UInt): QuizWithId
    fun getAllQuizzes(): List<QuizWithId>
    fun solveQuizWith(id: UInt, answer: Int): AnswerResult
}