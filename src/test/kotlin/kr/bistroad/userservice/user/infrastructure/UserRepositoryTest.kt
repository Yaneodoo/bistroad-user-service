package kr.bistroad.userservice.user.infrastructure

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserCredential
import kr.bistroad.userservice.user.domain.UserRole
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
internal class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `Saves a user`() {
        val credential = UserCredential(password = "example")
        val user = User(
            credential = credential,
            username = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )
        userRepository.save(user)

        val foundUser = userRepository.findByIdOrNull(user.id!!)

        foundUser.shouldNotBeNull()
        foundUser.shouldBe(user)
        foundUser.id.shouldNotBeNull()
        foundUser.credential.shouldBe(credential)
        foundUser.credential.id.shouldNotBeNull()
        foundUser.credential.user.shouldBe(user)
    }

    @Test
    fun `Deletes a user`() {
        val user = User(
            credential = UserCredential(password = "example"),
            username = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )
        userRepository.save(user)

        val userId = user.id!!
        userRepository.deleteById(userId)

        userRepository.findById(userId).isPresent.shouldBeFalse()
    }
}