package kr.bistroad.userservice.user.infrastructure

import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ThumbnailUtils {
    fun optimize(bytes: ByteArray, minWidth: Int, minHeight: Int): ByteArray {
        val image = ByteArrayInputStream(bytes).use { ImageIO.read(it) }

        val outputStream = ByteArrayOutputStream()
        ByteArrayInputStream(bytes).use {
            val thumbnail = Thumbnails.of(it)

            if (image.width > image.height)
                thumbnail.width(minWidth)
            else
                thumbnail.height(minHeight)
            thumbnail.keepAspectRatio(true)
            thumbnail.toOutputStream(outputStream)
        }

        return outputStream.use { it.toByteArray() }
    }
}