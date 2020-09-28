package kr.bistroad.userservice.user.infrastructure

import kr.bistroad.userservice.global.util.toPage
import kr.bistroad.userservice.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : UserRepositoryCustom {
    override fun search(username: String?, pageable: Pageable): Page<User> {
        val query = Query().with(pageable)
        if (username != null) query.addCriteria(where(User::username).`is`(username))

        return mongoTemplate.find<User>(query).toPage(pageable)
    }
}