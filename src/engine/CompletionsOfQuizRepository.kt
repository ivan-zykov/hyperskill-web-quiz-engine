package engine

import org.springframework.data.jpa.repository.JpaRepository

interface CompletionsOfQuizRepository : JpaRepository<CompletionOfQuizEntity, Long>
