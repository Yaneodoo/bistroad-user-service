package kr.bistroad.userservice.user.infrastructure

import kr.bistroad.userservice.user.domain.User
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface UserRepository : MongoRepository<User, UUID>, UserRepositoryCustom {
    fun existsByUsername(username: String): Boolean
    fun removeById(id: UUID): Long
}