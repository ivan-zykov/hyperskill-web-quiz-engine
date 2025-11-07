package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        val result = quizService.checkAnswer(answerIdx = answer)

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
    fun getQuiz(): Pair<UInt, Quiz>
    fun checkAnswer(answerIdx: Int): AnswerResult
    fun addQuiz(quiz: Quiz): Pair<UInt, Quiz>
    fun getQuizWith(id: UInt): Pair<UInt, Quiz>
}