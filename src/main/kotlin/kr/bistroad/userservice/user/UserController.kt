package kr.bistroad.userservice.user

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import kr.bistroad.userservice.exception.UserNotFoundException
import kr.bistroad.userservice.security.UserPrincipal
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["/users"])
class UserController(
    private val userService: UserService
) {
    @GetMapping("/users/{id}")
    @ApiOperation("\${swagger.doc.operation.user.get-user.description}")
    fun getUser(@PathVariable id: UUID) =
        userService.readUser(id) ?: throw UserNotFoundException()

    @GetMapping("/users/me")
    @ApiOperation("\${swagger.doc.operation.user.get-user-me.description}")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", required = true, paramType = "header",
        allowEmptyValue = false, dataTypeClass = String::class, example = "Bearer access_token"
    )
    @PreAuthorize("isAuthenticated()")
    fun getUser(): UserDto.CruRes {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        return userService.readUser(principal.userId) ?: throw UserNotFoundException()
    }

    @GetMapping("/users")
    @ApiOperation("\${swagger.doc.operation.user.get-users.description}")
    fun getUsers(dto: UserDto.SearchReq, pageable: Pageable) = userService.searchUsers(dto, pageable)

    @PostMapping("/users")
    @ApiOperation("\${swagger.doc.operation.user.post-user.description}")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", paramType = "header",
        dataTypeClass = String::class, example = "Bearer access_token"
    )
    @PreAuthorize("( #dto.role.toString() != 'ROLE_ADMIN' ) or hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    fun postUser(@RequestBody dto: UserDto.CreateReq) = userService.createUser(dto)

    @PatchMapping("/users/{id}")
    @ApiOperation("\${swagger.doc.operation.user.patch-user.description}")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", required = true, paramType = "header",
        allowEmptyValue = false, dataTypeClass = String::class, example = "Bearer access_token"
    )
    @PreAuthorize(
        "isAuthenticated() and " +
                "(( #id == principal.userId ) or hasRole('ROLE_ADMIN')) and " +
                "( #dto.role == null or hasRole('ROLE_ADMIN') )"
    )
    fun patchUser(@PathVariable id: UUID, @RequestBody dto: UserDto.PatchReq) = userService.patchUser(id, dto)

    @DeleteMapping("/users/{id}")
    @ApiOperation("\${swagger.doc.operation.user.delete-user.description}")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", required = true, paramType = "header",
        allowEmptyValue = false, dataTypeClass = String::class, example = "Bearer access_token"
    )
    @PreAuthorize("isAuthenticated() and (( #id == principal.userId ) or hasRole('ROLE_ADMIN'))")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> =
        if (userService.deleteUser(id))
            ResponseEntity.noContent().build()
        else
            ResponseEntity.notFound().build()

    @PostMapping("/users/{id}/verify-password")
    @ApiOperation("\${swagger.doc.operation.user.verify-password.description}")
    fun verifyPassword(@PathVariable id: UUID, @RequestBody dto: UserDto.VerifyPasswordReq) =
        userService.verifyPassword(id, dto)
}