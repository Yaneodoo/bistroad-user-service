package kr.bistroad.userservice.user.presentation

import kr.bistroad.userservice.user.domain.UserRole

interface UserRequest {
    data class SearchParam(
        val username: String?
    )

    data class PostBody(
        val username: String,
        val password: String,
        val fullName: String,
        val phone: String,
        val role: UserRole
    )

    data class PatchBody(
        val username: String?,
        val password: String?,
        val fullName: String?,
        val phone: String?,
        val role: UserRole?
    )

    data class VerifyPasswordBody(
        val password: String
    )
}