package kr.bistroad.userservice.user.application

import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserRole
import java.util.*
import kr.bistroad.userservice.user.domain.Photo as DomainPhoto

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
        val role: UserRole,
        val photo: Photo?
    ) : UserDto {
        companion object {
            fun fromEntity(user: User) =
                ForResult(
                    id = user.id,
                    username = user.username,
                    fullName = user.fullName,
                    phone = user.phone,
                    role = user.role,
                    photo = user.photo?.let(::Photo)
                )
        }

        data class Photo(
            val sourceUrl: String,
            val thumbnailUrl: String
        ) {
            constructor(domain: DomainPhoto): this(domain.sourceUrl, domain.thumbnailUrl)
        }
    }
}