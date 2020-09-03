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

@Service
class UserService(
    private val userRepository: UserRepository
) {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    fun encodePassword(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    fun createUser(dto: UserDto.Create): UserDto.Response {
        if (userRepository.findAllByUsername(dto.username).isNotEmpty()) throw UsernameExistException()

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
        return UserDto.Response.fromEntity(user)
    }

    fun readUser(dto: UserDto.Read): UserDto.Response? {
        val user = userRepository.findByIdOrNull(dto.id) ?: return null
        return UserDto.Response.fromEntity(user)
    }

    fun searchUsers(dto: UserDto.Search, pageable: Pageable): List<UserDto.Response> {
        return userRepository.search(
            username = dto.username,
            pageable = pageable
        ).content
            .map(UserDto.Response.Companion::fromEntity)
    }

    fun updateUser(dto: UserDto.Update): UserDto.Response {
        val user = userRepository.findByIdOrNull(dto.id) ?: throw UserNotFoundException()

        if (dto.username != null) user.username = dto.username
        if (dto.password != null) user.credential.password = passwordEncoder.encode(dto.password)
        if (dto.fullName != null) user.fullName = dto.fullName
        if (dto.phone != null) user.phone = dto.phone
        if (dto.role != null) user.role = dto.role

        userRepository.save(user)
        return UserDto.Response.fromEntity(user)
    }

    fun deleteUser(dto: UserDto.Delete): Boolean {
        val numDeleted = userRepository.removeById(dto.id)
        return numDeleted > 0
    }

    fun verifyPassword(dto: UserDto.VerifyPassword): Boolean {
        val user = userRepository.findByIdOrNull(dto.id) ?: throw UserNotFoundException()
        return passwordEncoder.matches(dto.password, user.credential.password)
    }
}