package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
@RequestMapping("/api")
class QuizEngineController @Autowired constructor(val quizService: QuizService) {
    @GetMapping("/quiz")
    fun quiz(): ResponseEntity<QuizDto> {
        val quiz = quizService.getQuiz()

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(quiz.toDto())
    }
}

private fun Quiz.toDto() = QuizDto(
    title = title,
    text = text,
    options = options,
)