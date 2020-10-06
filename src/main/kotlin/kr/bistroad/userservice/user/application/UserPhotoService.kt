package kr.bistroad.userservice.user.application

import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import kr.bistroad.userservice.global.error.exception.InvalidFileTypeException
import kr.bistroad.userservice.global.error.exception.UserNotFoundException
import kr.bistroad.userservice.user.domain.Photo
import kr.bistroad.userservice.user.infrastructure.UserRepository
import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

@Service
class UserPhotoService(
    @Value("\${gcs.bucket-name:bistroad-kr-photo-bucket}")
    private val bucketName: String,

    @Autowired(required = false)
    private val storage: Storage? = null,

    private val userRepository: UserRepository
) {
    fun upload(userId: UUID, file: MultipartFile) {
        require(file.contentType in ALLOWED_CONTENT_TYPES) { throw InvalidFileTypeException() }
        check(storage != null)

        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        val thumbnailOutputStream = ByteArrayOutputStream()
        Thumbnails.of(file.inputStream)
            .width(RESIZE_WIDTH)
            .keepAspectRatio(true)
            .toOutputStream(thumbnailOutputStream)
        val thumbnailInputStream = ByteArrayInputStream(thumbnailOutputStream.toByteArray())

        val sourceBlob = createBlobFrom(file.inputStream, randomNameFor(file))
        val thumbnailBlob = createBlobFrom(thumbnailInputStream, randomNameFor(file))

        user.photo = Photo(
            sourceUrl = "$PUBLIC_URL/$bucketName/${sourceBlob.name}",
            thumbnailUrl = "$PUBLIC_URL/$bucketName/${thumbnailBlob.name}"
        )
        userRepository.save(user)
    }

    private fun createBlobFrom(inputStream: InputStream, fileName: String) =
        storage!!.createFrom(
            BlobInfo.newBuilder(bucketName, fileName)
                .setAcl(listOf(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                .build(),
            inputStream
        )

    private fun randomNameFor(file: MultipartFile) =
        RandomStringUtils.randomAlphanumeric(8) + '.' + FilenameUtils.getExtension(file.originalFilename!!)

    companion object {
        val ALLOWED_CONTENT_TYPES: List<String> = listOf(
            "image/png", "image/jpeg", "image/gif"
        )

        const val PUBLIC_URL = "https://storage.googleapis.com"
        const val RESIZE_WIDTH = 100
    }
}