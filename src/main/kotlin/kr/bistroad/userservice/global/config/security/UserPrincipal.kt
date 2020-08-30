package kr.bistroad.userservice.global.config.security

import kr.bistroad.userservice.user.domain.UserRole
import java.util.*

data class UserPrincipal(
    val userId: UUID,
    val role: UserRole
)