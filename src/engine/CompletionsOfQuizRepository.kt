package engine

import org.springframework.data.jpa.repository.JpaRepository

interface CompletionsOfQuizRepository : JpaRepository<CompletionOfQuizEntity, Long> {
    fun findByQuiz(quiz: QuizEntity): List<CompletionOfQuizEntity>
}
