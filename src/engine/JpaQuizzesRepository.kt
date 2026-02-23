package engine

import org.springframework.data.repository.CrudRepository

interface JpaQuizzesRepository : CrudRepository<QuizEntity, Long>