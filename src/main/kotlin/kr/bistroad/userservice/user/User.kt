package kr.bistroad.userservice.user

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
        @Id
        @GeneratedValue(generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        @Column(columnDefinition = "BINARY(16)")
        val id: UUID? = null,

        var username: String,
        var fullName: String,
        var phone: String,
        var role: UserRole
)