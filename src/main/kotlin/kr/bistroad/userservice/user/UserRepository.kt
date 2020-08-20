package kr.bistroad.userservice.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID>, UserRepositoryCustom {
    fun findAllByUsername(username: String): List<User>

    @Transactional
    fun removeById(id: UUID): Long
}