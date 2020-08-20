package kr.bistroad.userservice.user

import com.querydsl.core.BooleanBuilder
import kr.bistroad.userservice.user.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl : QuerydslRepositorySupport(User::class.java), UserRepositoryCustom {
    override fun search(dto: UserDto.SearchReq, pageable: Pageable): Page<User> {
        val booleanBuilder = BooleanBuilder()
        if (dto.username != null) booleanBuilder.and(user.username.eq(dto.username))

        val query = from(user)
            .where(booleanBuilder)

        val list = querydsl!!.applyPagination(pageable, query).fetch()
        return PageImpl(list, pageable, query.fetchCount())
    }
}