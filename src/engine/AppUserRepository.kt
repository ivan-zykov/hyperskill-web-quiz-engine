package engine

import org.springframework.data.repository.CrudRepository

interface AppUserRepository : CrudRepository<AppUser, Int> {
    fun findByUsername(username: String): AppUser?
}
