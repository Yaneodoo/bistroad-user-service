package kr.bistroad.userservice.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepositoryCustom {
    fun search(dto: UserDto.SearchReq, pageable: Pageable): Page<User>
}