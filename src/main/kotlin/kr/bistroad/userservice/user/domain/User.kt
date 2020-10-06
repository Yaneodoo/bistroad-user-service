package kr.bistroad.userservice.user.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    var credential: UserCredential,
    var username: String,
    var fullName: String,
    var phone: String,
    var role: UserRole,
    var photo: Photo? = null
)