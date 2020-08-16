package kr.bistroad.userservice.security

import kr.bistroad.userservice.user.UserRole
import java.util.*

data class UserPrincipal(
    val userId: UUID,
    val role: UserRole
)