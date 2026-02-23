package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

@Repository
@Profile("jpa")
class JpaQuizzesRepositoryAdapter @Autowired constructor(
    private val jpa: JpaQuizzesRepository
) : QuizzesRepository {
    override fun addQuiz(newQuiz: NewQuiz): Quiz {
        val entity = newQuiz.toEntity()
        val savedEntity = jpa.save(entity)
        checkNotNull(savedEntity) { "Error. Failed to save new quiz" }

        return savedEntity.toDomain()
    }

    override fun findQuizBy(id: QuizId): Quiz =
        jpa.findById(id.value.toLong())
            .orElseThrow { QuizNotFoundException("Error. Failed to fetch quiz with ID: {$id.value}") }
            .toDomain()

    override fun getAllQuizzes(): List<Quiz> =
        jpa.findAll()
            .map { it.toDomain() }

    override fun reset() = jpa.deleteAll()
}

private fun NewQuiz.toEntity(): QuizEntity {
    val entity = QuizEntity()
    entity.title = this.title
    entity.text = this.text
    entity.options = this.options
    entity.answers = this.answer

    return entity
}

private fun QuizEntity.toDomain(): Quiz {
    return Quiz(
        title = this.title ?: "",
        text = this.text ?: "",
        options = this.options ?: emptyList(),
        answer = this.answers,
        id = QuizId(this.id?.toInt() ?: -1)
    )
}
