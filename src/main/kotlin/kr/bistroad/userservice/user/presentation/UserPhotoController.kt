package kr.bistroad.userservice.user.presentation

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kr.bistroad.userservice.user.application.UserPhotoService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@Api(tags = ["/users/*/photo"])
class UserPhotoController(
    private val userPhotoService: UserPhotoService
) {
    @PostMapping("/users/{id}/photo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ApiOperation("\${swagger.doc.operation.user.post-user-photo.description}")
    @PreAuthorize("isAuthenticated() and (( #id == principal.userId ) or hasRole('ROLE_ADMIN'))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun postPhoto(@PathVariable id: UUID, @RequestPart file: MultipartFile) {
        userPhotoService.upload(id, file)
    }
}