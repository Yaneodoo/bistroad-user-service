package kr.bistroad.userservice.user

import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UserController(
        private val userService: UserService
) {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: UUID) = userService.readUser(id)

    @GetMapping("/users")
    fun getUsers(@RequestParam dto: UserDto.SearchReq) = userService.searchUsers(dto)

    @PostMapping("/users")
    fun createUser(@RequestBody dto: UserDto.CreateReq) = userService.createUser(dto)

    @PatchMapping("/users/{id}")
    fun patchUser(@PathVariable id: UUID, @RequestBody dto: UserDto.PatchReq) = userService.patchUser(id, dto)

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: UUID) = userService.deleteUser(id)
}