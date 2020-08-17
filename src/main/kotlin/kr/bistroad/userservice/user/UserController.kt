package kr.bistroad.userservice.user

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: UUID) = userService.readUser(id)

    @GetMapping("/users")
    fun getUsers(dto: UserDto.SearchReq?) = userService.searchUsers(dto)

    @PostMapping("/users")
    @PreAuthorize("( #dto.role.toString() != 'ROLE_ADMIN' ) or hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody dto: UserDto.CreateReq) = userService.createUser(dto)

    @PatchMapping("/users/{id}")
    @PreAuthorize(
        "isAuthenticated() and " +
                "(( #id == principal.userId ) or hasRole('ROLE_ADMIN')) and " +
                "( #dto.role == null or hasRole('ROLE_ADMIN') )"
    )
    fun patchUser(@PathVariable id: UUID, @RequestBody dto: UserDto.PatchReq) = userService.patchUser(id, dto)

    @DeleteMapping("/users/{id}")
    @PreAuthorize("isAuthenticated() and (( #id == principal.userId ) or hasRole('ROLE_ADMIN'))")
    fun deleteUser(@PathVariable id: UUID) = userService.deleteUser(id)
}