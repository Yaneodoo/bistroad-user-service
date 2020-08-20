package kr.bistroad.userservice.user

import kr.bistroad.userservice.exception.UserNotFoundException
import kr.bistroad.userservice.exception.UsernameExistException
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

    fun createUser(dto: UserDto.CreateReq): UserDto.CruRes {
        if (userRepository.findAllByUsername(dto.username).isNotEmpty()) throw UsernameExistException()

        val credential = UserCredential(password = passwordEncoder.encode(dto.password))
        val user = User(
            credential = credential,
            username = dto.username,
            fullName = dto.fullName,
            phone = dto.phone,
            role = dto.role
        )
        credential.user = user

        userRepository.save(user)
        return UserDto.CruRes.fromEntity(user)
    }

    fun readUser(id: UUID): UserDto.CruRes? {
        val user = userRepository.findByIdOrNull(id) ?: return null
        return UserDto.CruRes.fromEntity(user)
    }

    fun searchUsers(dto: UserDto.SearchReq?): List<UserDto.CruRes> {
        val users = if (dto?.username != null)
            userRepository.findAllByUsername(dto.username)
        else
            userRepository.findAll()

        return users.map(UserDto.CruRes.Companion::fromEntity)
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