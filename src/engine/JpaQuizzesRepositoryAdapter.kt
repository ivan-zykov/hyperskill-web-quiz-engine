package engine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class JpaQuizzesRepositoryAdapter(@Autowired private val jpaQuizRepo: JpaQuizzesRepository) {

    fun addQuiz(entity: QuizEntity): QuizEntity {
        return jpaQuizRepo.save(entity)
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
