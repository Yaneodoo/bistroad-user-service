package kr.bistroad.userservice.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository
) {
    fun createUser(dto: UserDto.CreateReq): UserDto.CruRes {
        val user = User(
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

    fun searchUsers(): List<UserDto.CruRes> {
        return userRepository.findAll()
                .map(UserDto.CruRes.Companion::fromEntity)
    }

    fun patchUser(id: UUID, dto: UserDto.PatchReq): UserDto.CruRes {
        val user = userRepository.findByIdOrNull(id) ?: error("User not found")

        if (dto.username != null) user.username = dto.username
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
}