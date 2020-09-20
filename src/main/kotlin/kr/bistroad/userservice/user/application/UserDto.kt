package kr.bistroad.userservice.user.application

import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserRole
import java.util.*

interface UserDto {
    data class ForCreate(
        val username: String,
        val password: String,
        val fullName: String,
        val phone: String,
        val role: UserRole
    ) : UserDto

    data class ForUpdate(
        val username: String?,
        val password: String?,
        val fullName: String?,
        val phone: String?,
        val role: UserRole?
    ) : UserDto

    data class ForResult(
        val id: UUID,
        val username: String,
        val fullName: String,
        val phone: String,
        val role: UserRole
    ) : UserDto {
        companion object {
            fun fromEntity(user: User) =
                ForResult(
                    id = user.id!!,
                    username = user.username,
                    fullName = user.fullName,
                    phone = user.phone,
                    role = user.role
                )
        }
    }
}