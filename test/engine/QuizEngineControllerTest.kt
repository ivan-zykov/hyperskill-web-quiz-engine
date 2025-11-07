package engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.containsString
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

private const val TITLE = "test title"
private const val TEXT = "test text"
private const val OPTION = "test option"

private val quiz = QuizInDto(
    title = TITLE,
    text = TEXT,
    options = listOf(OPTION),
    answer = 0,
)

@SpringBootTest
@AutoConfigureMockMvc
class QuizEngineControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) {
    private val quizSerialized: String = mapper.writeValueAsString(quiz)

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
        mockMvc.perform(
            post("$API_PATH/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quizSerialized)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.text").value(TEXT))
            .andExpect(jsonPath("$.options[0]").value(OPTION))
            .andExpect(jsonPath("$.answer").doesNotExist())
    }

    @TestFactory
    fun `POST quizzes returns Bad request for bad supplied body`() = buildList {
        add("" to "body")

        val bodyMissingTitle = mapper.createObjectNode()
            .put("text", TEXT)
            .toString()
        add(bodyMissingTitle to "title")

        val bodyTitleNull = mapper.createObjectNode()
            .putNull("text")
            .toString()
        add(bodyTitleNull to "title")

        val bodyMissingAnswer = mapper.createObjectNode()
            .put("title", TITLE)
            .put("text", TEXT)
            .set<JsonNode>("options", mapper.valueToTree(listOf(OPTION)))
            .toString()
        add(bodyMissingAnswer to "answer")
    }.map { (body, expectedSubstring) ->
        dynamicTest("like: $body") {
            mockMvc.perform(
                post("$API_PATH/quizzes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error", containsString(expectedSubstring)))
        }
    }

    @Test
    fun `GET quizzes by id returns OK with one quiz`() {
        val addedQuizId = addQuiz(quizSerialized).id

        mockMvc.perform(get("$API_PATH/quizzes/${addedQuizId}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.text").value(TEXT))
            .andExpect(jsonPath("$.options[0]").value(OPTION))
            .andExpect(jsonPath("$.answer").doesNotExist())
    }

    @Test
    fun `GET quizzes by id returns Not found for non-existing id`() {
        val quizId = 0

        mockMvc.perform(get("$API_PATH/quizzes/$quizId"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                jsonPath("$.error")
                    .value("Error. There is no quiz with id $quizId.")
            )
    }

    private fun addQuiz(quizSerialized: String): QuizOutDto {
        val result = mockMvc.perform(
            post("$API_PATH/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quizSerialized)
        )
            .andReturn()

        val createdQuiz = mapper.readValue(
            result.response.contentAsByteArray,
            QuizOutDto::class.java
        )
        checkNotNull(createdQuiz) { "Error. Failed to add quiz $quizSerialized" }

        return createdQuiz
    }

}