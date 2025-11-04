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
}

private fun Quiz.toDto() = QuizOutDto(
    id = id,
    title = title,
    text = text,
    options = options,
)

private fun AnswerResult.toDto() = ResultDto(
    success = success,
    feedback = feedback,
)