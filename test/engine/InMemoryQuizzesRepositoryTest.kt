package engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class InMemoryQuizzesRepositoryTest {
    private val sut = @Autowired InMemoryQuizzesRepository()

    private val quiz1Id = 331833382
    private val quiz1 = Quiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug"),
        answer = 2,
    )

    @BeforeEach
    fun setUp() {
        sut.reset()
    }

    @Test
    fun `Adds a quiz`() {
        val (id, _) = sut.addQuiz(quiz = quiz1)

        val actual = sut.findQuizWith(id)
        assertAll(
            { assertEquals(quiz1Id, actual.first) },
            { assertEquals(quiz1.title, actual.second.title) },
            { assertEquals(quiz1.text, actual.second.text) },
            { assertEquals(quiz1.options, actual.second.options) },
            { assertEquals(quiz1.answer, actual.second.answer) }
        )
    }

    @Test
    fun `Gets quiz with ID`() {
        sut.addQuiz(quiz1)

        val actual = sut.findQuizWith(id = quiz1Id)

        assertAll(
            { assertEquals(quiz1Id, actual.first) },
            { assertEquals(quiz1.title, actual.second.title) },
            { assertEquals(quiz1.text, actual.second.text) },
            { assertEquals(quiz1.options, actual.second.options) },
            { assertEquals(quiz1.answer, actual.second.answer) }
        )
    }

    @Test
    fun `Getting non-existent quiz throws exception`() {
        val nonExistentId = 0
        val exception = assertThrows<QuizNotFoundException> {
            sut.findQuizWith(id = nonExistentId)
        }
        assertTrue {
            exception.message!!.contains(nonExistentId.toString())
        }
    }

    @Test
    fun `Gets zero quizzes`() {
        val actual = sut.getAllQuizzes()

        assertEquals(listOf<QuizWithId>(), actual)
    }

    @Test
    fun `Gets two quizzes`() {
        sut.addQuiz(quiz = quiz1)
        val quiz2 = quiz1.copy(title = "The Java Logo 2")
        val quiz2Id = 1064299156
        sut.addQuiz(quiz = quiz2)

        val actual = sut.getAllQuizzes()

        assertAll(
            { assertEquals(2, actual.size) },
            { assertEquals(quiz1Id, actual[0].first) },
            { assertEquals(quiz1.title, actual[0].second.title) },
            { assertEquals(quiz1.text, actual[0].second.text) },
            { assertEquals(quiz1.options, actual[0].second.options) },
            { assertEquals(quiz1.answer, actual[0].second.answer) },
            { assertEquals(quiz2Id, actual[1].first) },
            { assertEquals(quiz2.title, actual[1].second.title) },
            { assertEquals(quiz2.text, actual[1].second.text) },
            { assertEquals(quiz2.options, actual[1].second.options) },
            { assertEquals(quiz2.answer, actual[1].second.answer) }
        )
    }

    @Test
    fun `Removes all saved quizzes`() {
        sut.addQuiz(quiz1)
        sut.addQuiz(quiz1.copy(title = "Another title"))
        assertTrue { sut.getAllQuizzes().size == 2 }

        sut.reset()

        assertTrue { sut.getAllQuizzes().isEmpty() }
    }
}