package engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

private const val API_PATH = "/api"

private const val TITLE = "test title"
private const val TEXT = "test text"
private const val OPTION = "test option"

private const val CONGRATULATIONS = "Congratulations, you're right!"

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
    private val quizzesRepository: InMemoryQuizzesRepository,
    private val mapper: ObjectMapper
) {
    private val quizSerialized1: String = mapper.writeValueAsString(quiz)

    @BeforeEach
    fun reset() {
        quizzesRepository.reset()
    }

    @Test
    fun `GET quiz returns OK with one quiz`() {
        mockMvc.get("$API_PATH/quiz")
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.title") { value("The Java Logo") }
                jsonPath("$.text") { value("What is depicted on the Java logo?") }
                jsonPath("$.options") { isArray() }
                jsonPath("$.options[0]") { value("Robot") }
                jsonPath("$.options[1]") { value("Tea leaf") }
                jsonPath("$.options[2]") { value("Cup of coffee") }
                jsonPath("$.options[3]") { value("Bug") }
            }
    }

    @Test
    fun `POST quiz returns OK for correct answer`() {
        mockMvc.post("$API_PATH/quiz?answer=2")
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.success") { value(true) }
                jsonPath("$.feedback") { value(CONGRATULATIONS) }
            }
    }

    @TestFactory
    fun `POST quiz returns Bad request with`() = listOf(
        "",
        "a=2",
    ).map { requestParam ->
        dynamicTest("request param: $requestParam") {
            mockMvc.post("$API_PATH/quiz?$requestParam")
                .andExpectAll {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.error") { value(containsString("parameter")) }
                    jsonPath("$.error") { value(containsString("answer")) }
                }
        }
    }

    @Test
    fun `POST quizzes returns OK with created quiz`() {
        mockMvc.post("$API_PATH/quizzes") {
            contentType = MediaType.APPLICATION_JSON
            content = quizSerialized1
        }
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { isNumber() }
                jsonPath("$.title") { TITLE }
                jsonPath("$.text") { TEXT }
                jsonPath("$.options[0]") { OPTION }
                jsonPath("$.answer") { doesNotExist() }
            }
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
    }.map { (body, missingField) ->
        dynamicTest("like: $body") {
            mockMvc.post("$API_PATH/quizzes") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }
                .andExpectAll {
                    status { isBadRequest() }
                    jsonPath("$.error") { value(containsString(missingField)) }
                }
        }
    }

    @Test
    fun `GET quizzes by id returns OK with one quiz`() {
        val addedQuizId = addQuiz(quizSerialized1).id.value

        mockMvc.get("$API_PATH/quizzes/${addedQuizId}")
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { isNumber() }
                jsonPath("$.title") { value(TITLE) }
                jsonPath("$.text") { value(TEXT) }
                jsonPath("$.options[0]") { value(OPTION) }
                jsonPath("$.answer") { doesNotExist() }
            }
    }

    @Test
    fun `GET quizzes by id returns Not found for non-existing id`() {
        val quizId = 0

        mockMvc.get("$API_PATH/quizzes/$quizId")
            .andExpectAll {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.error") { value(containsString("Error")) }
                jsonPath("$.error") { value(containsString(quizId.toString())) }
            }
    }

    @Test
    fun `GET all quizzes returns empty array`() {
        mockMvc.get("$API_PATH/quizzes")
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$") { isArray() }
                jsonPath("$") { isEmpty() }
            }
    }

    @Test
    fun `GET all quizzes returns list with two`() {
        addQuiz(quizSerialized1)
        val quizSerialized2 = mapper.writeValueAsString(quiz.copy(title = "$TITLE 2"))
        addQuiz(quizSerialized2)

        val result = mockMvc.get("$API_PATH/quizzes")
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
            .andReturn()

        val returnedQuizzes: List<QuizOutDto> = mapper.readValue(result.response.contentAsString)
        assertNotEquals(returnedQuizzes[0].id, returnedQuizzes[1].id)
        assertTrue(returnedQuizzes.all { it.title.isNotEmpty() })
        assertTrue(returnedQuizzes.all { it.text == TEXT })
        assertTrue(returnedQuizzes.all { it.options.first() == OPTION })
    }

    @Test
    fun `POST quizzes-id-solve returns OK`() {
        val idOfAddedQuiz = addQuiz(quizSerialized1).id.value

        mockMvc.post("$API_PATH/quizzes/{id}/solve", idOfAddedQuiz) {
            param("answer", 0.toString())
        }
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.success") { value(true) }
                jsonPath("$.feedback") { value(CONGRATULATIONS) }
            }
    }

    @Test
    fun `POST quizzes-id-solve returns Not found for non-existing quiz`() {
        val idOfNonExistingQuiz = 1

        mockMvc.post("$API_PATH/quizzes/{id}/solve", idOfNonExistingQuiz) {
            param("answer", 0.toString())
        }.andExpectAll {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.error") { value(containsString("Error")) }
            jsonPath("$.error") { value(containsString(idOfNonExistingQuiz.toString())) }
        }
    }

    private fun addQuiz(quizSerialized: String): QuizOutDto {
        val result = mockMvc.perform(
            post("$API_PATH/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(quizSerialized)
        )
            .andReturn()

        val createdQuiz: QuizOutDto? = mapper.readValue(result.response.contentAsByteArray)
        checkNotNull(createdQuiz) { "Error. Failed to add quiz $quizSerialized" }

        return createdQuiz
    }

}