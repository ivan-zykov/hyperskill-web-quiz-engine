package engine

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

private const val API_PATH = "/api"

@SpringBootTest
@AutoConfigureMockMvc
class QuizEngineControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) {
    @Test
    fun `GET quiz returns OK with one quiz`() {
        mockMvc.perform(get("$API_PATH/quiz"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("The Java Logo"))
            .andExpect(jsonPath("$.text").value("What is depicted on the Java logo?"))
            .andExpect(jsonPath("$.options").isArray)
            .andExpect(jsonPath("$.options[0]").value("Robot"))
            .andExpect(jsonPath("$.options[1]").value("Tea leaf"))
            .andExpect(jsonPath("$.options[2]").value("Cup of coffee"))
            .andExpect(jsonPath("$.options[3]").value("Bug"))
    }

    @Test
    fun `POST quiz returns OK for correct answer`() {
        mockMvc.perform(post("$API_PATH/quiz?answer=2"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.feedback").value("Congratulations, you're right!"))
    }

    @TestFactory
    fun `POST quiz returns Bad request with`() = listOf(
        "",
        "a=2",
    ).map { requestParam ->
        dynamicTest("request param: $requestParam") {
            mockMvc.perform(post("$API_PATH/quiz?$requestParam"))
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isString)
        }
    }

    @Test
    fun `POST quizzes returns OK with created quiz`() {
        val title = "test title"
        val text = "test text"
        val option = "test option"
        val quiz = QuizInDto(
            title = title,
            text = text,
            options = listOf(option),
            answer = 0,
        )
        val quizSerialized = mapper.writeValueAsString(quiz)

        mockMvc.perform(
            post("$API_PATH/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quizSerialized)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.text").value(text))
            .andExpect(jsonPath("$.options[0]").value(option))
            .andExpect(jsonPath("$.answer").doesNotExist())
    }

}