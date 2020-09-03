package kr.bistroad.userservice.user.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kr.bistroad.userservice.global.error.exception.UserNotFoundException
import kr.bistroad.userservice.global.error.exception.UsernameExistException
import kr.bistroad.userservice.user.application.UserDto.*
import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserCredential
import kr.bistroad.userservice.user.domain.UserRole
import kr.bistroad.userservice.user.infrastructure.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.util.*

@ExtendWith(MockKExtension::class)
internal class UserServiceTest {
    @InjectMockKs
    private lateinit var userService: UserService

    @MockK
    private lateinit var userRepository: UserRepository

    @Test
    fun `Throws error when creating with a already taken username`() {
        val dto = CreateReq(
            username = "John",
            password = "example",
            fullName = "example",
            phone = "example",
            role = UserRole.ROLE_USER
        )
        every { userRepository.findAllByUsername("John") } returns listOf(
            User(
                username = "John", credential = UserCredential(password = "example"),
                fullName = "example", phone = "010-0000-0000", role = UserRole.ROLE_USER
            ).apply { credential.user = this }
        )

        shouldThrow<UsernameExistException> {
            userService.createUser(dto)
        }
    }

    @Test
    fun `Throws error when patching with an invalid user id`() {
        val userId = UUID.randomUUID()
        val dto = PatchReq(
            username = "example",
            password = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )
        every { userRepository.findByIdOrNull(userId) } returns null

        shouldThrow<UserNotFoundException> {
            userService.patchUser(userId, dto)
        }
    }

    @Test
    fun `Verifies passwords`() {
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            credential = UserCredential(password = userService.encodePassword("QrX2RqCpqY")),
            username = "example",
            fullName = "example",
            phone = "example",
            role = UserRole.ROLE_USER
        ).apply { credential.user = this }

        every { userRepository.findByIdOrNull(userId) } returns user
        every { userRepository.findByIdOrNull(not(userId)) } returns null

        userService.verifyPassword(userId, VerifyPasswordReq("QrX2RqCpqY")).shouldBeTrue()

        userService.verifyPassword(userId, VerifyPasswordReq("")).shouldBeFalse()
        userService.verifyPassword(userId, VerifyPasswordReq("QrX2RqCpq")).shouldBeFalse()
        userService.verifyPassword(userId, VerifyPasswordReq("QrX2RqCpqYa")).shouldBeFalse()
        userService.verifyPassword(userId, VerifyPasswordReq("qrx2rqcpqy")).shouldBeFalse()

        shouldThrow<UserNotFoundException> {
            userService.verifyPassword(UUID.randomUUID(), VerifyPasswordReq("QrX2RqCpqY"))
        }
    }
}