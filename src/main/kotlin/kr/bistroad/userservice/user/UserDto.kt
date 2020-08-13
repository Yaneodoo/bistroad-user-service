package kr.bistroad.userservice.user

import java.util.*

interface UserDto {
    data class CreateReq(
            val username: String,
            val password: String,
            val fullName: String,
            val phone: String,
            val role: UserRole
    )

    data class PatchReq(
            val username: String?,
            val password: String?,
            val fullName: String?,
            val phone: String?,
            val role: UserRole?
    )

    data class CruRes(
            val id: UUID,
            val username: String,
            val fullName: String,
            val phone: String,
            val role: UserRole
    ) {
        companion object {
            fun fromEntity(user: User) = CruRes(
                    id = user.id!!,
                    username = user.username,
                    fullName = user.fullName,
                    phone = user.phone,
                    role = user.role
            )
        }
    }
}