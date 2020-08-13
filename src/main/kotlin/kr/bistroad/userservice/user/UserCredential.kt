package kr.bistroad.userservice.user

import javax.persistence.*

@Entity
@Table(name = "user_credentials")
class UserCredential(
        @Id
        @GeneratedValue
        val id: Long? = null,

        var password: String,

        @OneToOne(mappedBy = "credential")
        var user: User? = null
)