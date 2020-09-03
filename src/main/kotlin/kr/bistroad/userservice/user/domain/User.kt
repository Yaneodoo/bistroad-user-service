package kr.bistroad.userservice.user.domain

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

    credential: UserCredential,

    var username: String,
    var fullName: String,
    var phone: String,
    var role: UserRole
) {
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "credentialId")
    val credential: UserCredential = credential.apply { user = this@User }
}