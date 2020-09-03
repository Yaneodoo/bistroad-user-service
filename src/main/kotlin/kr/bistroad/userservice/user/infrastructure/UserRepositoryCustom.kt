package kr.bistroad.userservice.user.infrastructure

import kr.bistroad.userservice.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepositoryCustom {
    fun search(username: String?, pageable: Pageable): Page<User>
}