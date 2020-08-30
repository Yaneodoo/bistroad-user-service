package kr.bistroad.userservice.user.infrastructure

import kr.bistroad.userservice.user.application.UserDto
import kr.bistroad.userservice.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepositoryCustom {
    fun search(dto: UserDto.SearchReq, pageable: Pageable): Page<User>
}