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

    fun createUser(dto: UserDto.CreateReq): UserDto.CruRes {
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
        return UserDto.CruRes.fromEntity(user)
    }

    fun readUser(id: UUID): UserDto.CruRes? {
        val user = userRepository.findByIdOrNull(id) ?: return null
        return UserDto.CruRes.fromEntity(user)
    }

    fun searchUsers(dto: UserDto.SearchReq, pageable: Pageable): List<UserDto.CruRes> {
        return userRepository.search(dto, pageable)
            .content.map(UserDto.CruRes.Companion::fromEntity)
    }

    fun patchUser(id: UUID, dto: UserDto.PatchReq): UserDto.CruRes {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        if (dto.username != null) user.username = dto.username
        if (dto.password != null) user.credential.password = passwordEncoder.encode(dto.password)
        if (dto.fullName != null) user.fullName = dto.fullName
        if (dto.phone != null) user.phone = dto.phone
        if (dto.role != null) user.role = dto.role

        userRepository.save(user)
        return UserDto.CruRes.fromEntity(user)
    }

    fun deleteUser(id: UUID): Boolean {
        val numDeleted = userRepository.removeById(id)
        return numDeleted > 0
    }

    fun verifyPassword(id: UUID, dto: UserDto.VerifyPasswordReq): Boolean {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return passwordEncoder.matches(dto.password, user.credential.password)
    }
}