package kr.bistroad.userservice.user.application

import kr.bistroad.userservice.global.error.exception.UserNotFoundException
import kr.bistroad.userservice.global.error.exception.UsernameExistException
import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserCredential
import kr.bistroad.userservice.user.infrastructure.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    fun encodePassword(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    fun createUser(dto: UserDto.ForCreate): UserDto.ForResult {
        check(!userRepository.existsByUsername(dto.username)) { throw UsernameExistException() }

        val user = User(
            credential = UserCredential(
                password = encodePassword(dto.password)
            ),
            username = dto.username,
            fullName = dto.fullName,
            phone = dto.phone,
            role = dto.role
        )

        userRepository.save(user)
        return UserDto.ForResult.fromEntity(user)
    }

    fun readUser(id: UUID): UserDto.ForResult? {
        val user = userRepository.findByIdOrNull(id) ?: return null
        return UserDto.ForResult.fromEntity(user)
    }

    fun searchUsers(
        username: String?,
        pageable: Pageable
    ): List<UserDto.ForResult> =
        userRepository.search(
            username = username,
            pageable = pageable
        ).content
            .map(UserDto.ForResult.Companion::fromEntity)

    fun updateUser(id: UUID, dto: UserDto.ForUpdate): UserDto.ForResult {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        if (dto.username != null) user.username = dto.username
        if (dto.password != null) user.credential.password = passwordEncoder.encode(dto.password)
        if (dto.fullName != null) user.fullName = dto.fullName
        if (dto.phone != null) user.phone = dto.phone
        if (dto.role != null) user.role = dto.role

        userRepository.save(user)
        return UserDto.ForResult.fromEntity(user)
    }

    fun deleteUser(id: UUID): Boolean {
        val numDeleted = userRepository.removeById(id)
        return numDeleted > 0
    }

    fun verifyPassword(id: UUID, password: String): Boolean {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return passwordEncoder.matches(password, user.credential.password)
    }
}