package kr.bistroad.userservice.user.infrastructure

import com.querydsl.core.BooleanBuilder
import kr.bistroad.userservice.user.domain.QUser.user
import kr.bistroad.userservice.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl : QuerydslRepositorySupport(User::class.java),
    UserRepositoryCustom {
    override fun search(username: String?, pageable: Pageable): Page<User> {
        val booleanBuilder = BooleanBuilder()
        if (username != null) booleanBuilder.and(user.username.eq(username))

        val query = from(user)
            .where(booleanBuilder)

        val list = querydsl!!.applyPagination(pageable, query).fetch()
        return PageImpl(list, pageable, query.fetchCount())
    }
}