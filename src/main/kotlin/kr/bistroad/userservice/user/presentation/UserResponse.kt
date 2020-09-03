package kr.bistroad.userservice.user.presentation

import kr.bistroad.userservice.user.application.UserDto
import kr.bistroad.userservice.user.domain.UserRole
import java.util.*

class UserResponse(
    val id: UUID,
    val username: String,
    val fullName: String,
    val phone: String,
    val role: UserRole
) {
    companion object {
        fun fromDto(dto: UserDto.Response) = UserResponse(
            id = dto.id,
            username = dto.username,
            fullName = dto.fullName,
            phone = dto.phone,
            role = dto.role
        )
    }
}