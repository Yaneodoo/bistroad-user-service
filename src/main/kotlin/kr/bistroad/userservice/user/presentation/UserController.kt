package kr.bistroad.userservice.user.presentation

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kr.bistroad.userservice.global.config.security.UserPrincipal
import kr.bistroad.userservice.global.error.exception.InsufficientAuthorizationException
import kr.bistroad.userservice.global.error.exception.UserNotFoundException
import kr.bistroad.userservice.user.application.UserDto
import kr.bistroad.userservice.user.application.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["/users"])
class UserController(
    private val userService: UserService
) {
    @GetMapping("/users/{id}")
    @ApiOperation("\${swagger.doc.operation.user.get-user.description}")
    fun getUser(
        @PathVariable id: UUID
    ) = UserResponse.fromDto(
        userService.readUser(
            UserDto.Read(id)
        ) ?: throw UserNotFoundException()
    )

    @GetMapping("/users/me")
    @ApiOperation("\${swagger.doc.operation.user.get-user-me.description}")
    @PreAuthorize("isAuthenticated()")
    fun getUser() = UserResponse.fromDto(
        userService.readUser(
            UserDto.Read(
                id = UserPrincipal.ofCurrentContext().userId
                    ?: throw InsufficientAuthorizationException()
            )
        ) ?: throw UserNotFoundException()
    )

    @GetMapping("/users")
    @ApiOperation("\${swagger.doc.operation.user.get-users.description}")
    fun getUsers(
        param: UserRequest.SearchParam,
        pageable: Pageable
    ) = userService.searchUsers(
        UserDto.Search(
            username = param.username
        ),
        pageable
    ).map { UserResponse.fromDto(it) }

    @PostMapping("/users")
    @ApiOperation("\${swagger.doc.operation.user.post-user.description}")
    @PreAuthorize("( #body.role.toString() != 'ROLE_ADMIN' ) or hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    fun postUser(
        @RequestBody body: UserRequest.PostBody
    ) = UserResponse.fromDto(
        userService.createUser(
            UserDto.Create(
                username = body.username,
                password = body.password,
                fullName = body.fullName,
                phone = body.phone,
                role = body.role
            )
        )
    )

    @PatchMapping("/users/{id}")
    @ApiOperation("\${swagger.doc.operation.user.patch-user.description}")
    @PreAuthorize(
        "isAuthenticated() and (( #id == principal.userId ) or ( #body.role == null ) or hasRole('ROLE_ADMIN') )"
    )
    fun patchUser(
        @PathVariable id: UUID,
        @RequestBody body: UserRequest.PatchBody
    ) = UserResponse.fromDto(
        userService.updateUser(
            UserDto.Update(
                id = id,
                username = body.username,
                password = body.password,
                fullName = body.fullName,
                phone = body.phone,
                role = body.role
            )
        )
    )

    @DeleteMapping("/users/{id}")
    @ApiOperation("\${swagger.doc.operation.user.delete-user.description}")
    @PreAuthorize("isAuthenticated() and (( #id == principal.userId ) or hasRole('ROLE_ADMIN'))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @PathVariable id: UUID
    ) {
        val deleted = userService.deleteUser(UserDto.Delete(id))
        if (!deleted) throw UserNotFoundException()
    }

    @PostMapping("/users/{id}/verify-password")
    @ApiOperation("\${swagger.doc.operation.user.verify-password.description}")
    fun verifyPassword(
        @PathVariable id: UUID,
        @RequestBody body: UserRequest.VerifyPasswordBody
    ) = userService.verifyPassword(
        UserDto.VerifyPassword(id, body.password)
    )
}