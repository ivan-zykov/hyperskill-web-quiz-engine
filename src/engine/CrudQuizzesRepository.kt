package engine

import org.springframework.data.repository.CrudRepository

interface CrudQuizzesRepository : CrudRepository<QuizEntity, Long>
