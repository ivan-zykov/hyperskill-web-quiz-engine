package engine

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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

}