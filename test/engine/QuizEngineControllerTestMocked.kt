package engine

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class QuizEngineControllerTestMocked @Autowired constructor(private val mockMvc: MockMvc) {
    //    todo: move to not-mocked tests
    @Test
    fun `POST quiz returns OK for correct answer`() {
        mockMvc.perform(post("/api/quiz?answer=2"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.feedback").value("Test feedback"))
    }

    @TestConfiguration
    @Suppress("unused")
    class TestBeans {
        @Bean
        fun quizService(): QuizService = FakeQuizService()
    }

    class FakeQuizService : QuizService {
        override fun getQuiz(): Quiz = Quiz(title = "test", text = "test", options = listOf())

        override fun checkAnswer(answerIdx: Int): AnswerResult {
            return AnswerResult(
                success = true,
                feedback = "Test feedback",
            )
        }
    }
}