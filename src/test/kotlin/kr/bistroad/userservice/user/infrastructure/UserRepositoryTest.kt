package kr.bistroad.userservice.user.infrastructure

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserCredential
import kr.bistroad.userservice.user.domain.UserRole
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.repository.findByIdOrNull

@DataMongoTest
internal class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun clear() = userRepository.deleteAll()

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

        val foundUser = userRepository.findByIdOrNull(user.id)

        foundUser.shouldNotBeNull()
        foundUser.shouldBe(user)
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

        val userId = user.id
        userRepository.deleteById(userId)

        userRepository.findByIdOrNull(userId).shouldBeNull()
        userRepository.findAll().shouldBeEmpty()
    }
}