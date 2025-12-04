package engine

import org.springframework.beans.factory.annotation.Autowired
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
            .body(quiz.toDto())
    }

    @PostMapping("/quiz")
    fun answerQuiz(@RequestParam answer: Int): ResponseEntity<ResultDto> {
        val result = quizService.checkAnswer(answer = answer)

        return ResponseEntity
            .ok()
            .body(result.toDto())
    }

    @PostMapping("/quizzes")
    fun addQuiz(@RequestBody quiz: QuizInDto): ResponseEntity<QuizOutDto> {
        val createdIdToQuiz = quizService.addQuiz(quiz.toNewQuiz())

        return ResponseEntity
            .ok()
            .body(createdIdToQuiz.toDto())
    }

    @GetMapping("/quizzes/{id}")
    fun showQuiz(@PathVariable id: QuizId): ResponseEntity<QuizOutDto> {
        val quiz = quizService.getQuizWith(id)

        return ResponseEntity
            .ok()
            .body(quiz.toDto())
    }

    @GetMapping("/quizzes")
    fun showAllQuizzes(): ResponseEntity<List<QuizOutDto>> {
        val quizzes = quizService.getAllQuizzes()

        return ResponseEntity
            .ok()
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
    fun getQuiz(): Quiz
    fun checkAnswer(answer: Int): AnswerResult
    fun addQuiz(newQuiz: NewQuiz): Quiz
    fun getQuizWith(id: QuizId): Quiz
    fun getAllQuizzes(): List<Quiz>
    fun solveQuizWith(id: QuizId, answer: Int): AnswerResult
}