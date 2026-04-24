package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class JpaQuizzesRepositoryAdapter @Autowired constructor(
    private val jpaQuizRepo: JpaQuizzesRepository,
    private val userRepo: AppUserRepository,
) {
    fun addQuiz(newQuiz: NewQuiz): Quiz {
        val user = userRepo.findByUsername(newQuiz.authorUsername!!)
            ?: throw RuntimeException("Server error. User ${newQuiz.authorUsername} was not found.")
        val entity = newQuiz.toEntity(user)

        val savedEntity = jpaQuizRepo.save(entity)

        return savedEntity.toDomain()
    }

    fun findQuizBy(id: QuizId): Quiz =
        jpaQuizRepo.findById(id.value.toLong())
            .orElseThrow { QuizNotFoundException("Error. Quiz with ID: ${id.value} does not exist.") }
            .toDomain()

    fun getAllQuizzes(): List<Quiz> =
        jpaQuizRepo.findAll()
            .map { it.toDomain() }

    fun reset() {
        jpaQuizRepo.deleteAll()
    }

    fun deleteById(id: QuizId) {
        findQuizBy(id)
        jpaQuizRepo.deleteById(id.value.toLong())
    }
}

private fun NewQuiz.toEntity(user: AppUser): QuizEntity {
    val entity = QuizEntity()
    entity.title = this.title
    entity.text = this.text
    entity.options = this.options
    entity.answers = this.answer
    entity.author = user

    return entity
}

private fun QuizEntity.toDomain(): Quiz {
    return Quiz(
        title = this.title ?: "",
        text = this.text ?: "",
        options = this.options ?: emptyList(),
        answer = this.answers,
        id = QuizId(this.id?.toInt() ?: -1),
        authorUsername = this.author?.username ?: "",
    )
}
