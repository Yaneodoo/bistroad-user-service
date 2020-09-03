package kr.bistroad.userservice.global.config.security

import kr.bistroad.userservice.user.domain.UserRole
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

data class UserPrincipal(
    val userId: UUID,
    val role: UserRole
) {
    companion object {
        fun ofCurrentContext() =
            SecurityContextHolder.getContext().authentication.principal as UserPrincipal
    }
}