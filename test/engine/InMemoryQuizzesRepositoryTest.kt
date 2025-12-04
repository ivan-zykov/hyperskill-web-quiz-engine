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

    private val quiz1Id = QuizId(1)
    private val newQuiz1 = NewQuiz(
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
        val addedQuizId = sut.addQuiz(newQuiz = newQuiz1).id!!

        val actual = sut.findQuizBy(addedQuizId)
        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) }
        )
    }

    @Test
    fun `Gets quiz by ID`() {
        sut.addQuiz(newQuiz1)

        val actual = sut.findQuizBy(id = quiz1Id)

        assertAll(
            { assertEquals(quiz1Id, actual.id) },
            { assertEquals(newQuiz1.title, actual.title) },
            { assertEquals(newQuiz1.text, actual.text) },
            { assertEquals(newQuiz1.options, actual.options) },
            { assertEquals(newQuiz1.answer, actual.answer) }
        )
    }

    @Test
    fun `Getting non-existent quiz throws exception`() {
        val nonExistentId = QuizId(0)
        val exception = assertThrows<QuizNotFoundException> {
            sut.findQuizBy(id = nonExistentId)
        }
        assertTrue {
            exception.message!!.contains(nonExistentId.toString())
        }
    }

    @Test
    fun `Gets zero quizzes`() {
        val actual = sut.getAllQuizzes()

        assertEquals(listOf<Quiz>(), actual)
    }

    @Test
    fun `Gets two quizzes`() {
        sut.addQuiz(newQuiz = newQuiz1)
        val newQuiz2 = newQuiz1.copy(title = "The Java Logo 2")
        val quiz2Id = QuizId(2)
        sut.addQuiz(newQuiz = newQuiz2)

        val actual = sut.getAllQuizzes()

        assertAll(
            { assertEquals(2, actual.size) },
            { assertEquals(quiz1Id, actual[0].id) },
            { assertEquals(newQuiz1.title, actual[0].title) },
            { assertEquals(newQuiz1.text, actual[0].text) },
            { assertEquals(newQuiz1.options, actual[0].options) },
            { assertEquals(newQuiz1.answer, actual[0].answer) },
            { assertEquals(quiz2Id, actual[1].id) },
            { assertEquals(newQuiz2.title, actual[1].title) },
            { assertEquals(newQuiz2.text, actual[1].text) },
            { assertEquals(newQuiz2.options, actual[1].options) },
            { assertEquals(newQuiz2.answer, actual[1].answer) }
        )
    }

    @Test
    fun `Removes all saved quizzes`() {
        sut.addQuiz(newQuiz1)
        sut.addQuiz(newQuiz1.copy(title = "Another title"))
        assertTrue { sut.getAllQuizzes().size == 2 }

        sut.reset()

        assertTrue { sut.getAllQuizzes().isEmpty() }
    }
}