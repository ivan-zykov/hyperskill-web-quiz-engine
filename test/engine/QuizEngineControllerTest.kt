package engine

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

@SpringBootTest
@AutoConfigureMockMvc
class QuizEngineControllerTest @Autowired constructor(private val mockMvc: MockMvc) {
    @Test
    fun `GET quiz returns OK with one quiz`() {
        mockMvc.perform(get("/api/quiz"))
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
        mockMvc.perform(post("/api/quiz?answer=2"))
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
            mockMvc.perform(post("/api/quiz?$requestParam"))
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").isString)
        }
    }

}