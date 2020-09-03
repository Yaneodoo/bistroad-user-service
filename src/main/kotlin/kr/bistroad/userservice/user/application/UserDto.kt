package kr.bistroad.userservice.user.application

import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserRole
import java.util.*

interface UserDto {
    data class Create(
        val username: String,
        val password: String,
        val fullName: String,
        val phone: String,
        val role: UserRole
    )

    data class Read(
        val id: UUID
    )

    data class Search(
        val username: String?
    )

    data class Update(
        val id: UUID,
        val username: String?,
        val password: String?,
        val fullName: String?,
        val phone: String?,
        val role: UserRole?
    )

    data class Delete(
        val id: UUID
    )

    data class VerifyPassword(
        val id: UUID,
        val password: String
    )

    data class Response(
        val id: UUID,
        val username: String,
        val fullName: String,
        val phone: String,
        val role: UserRole
    ) {
        companion object {
            fun fromEntity(user: User) =
                Response(
                    id = user.id!!,
                    username = user.username,
                    fullName = user.fullName,
                    phone = user.phone,
                    role = user.role
                )
        }
    }
}