package org.briarproject.briar.desktop.attachment.media

import mu.KotlinLogging
import org.briarproject.briar.api.attachment.MediaConstants.MAX_IMAGE_SIZE
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.inject.Inject

class ImageCompressorImpl @Inject internal constructor() : ImageCompressor {

    companion object {
        val LOG = KotlinLogging.logger {}
    }

    override fun compressImage(image: BufferedImage): InputStream {
        val out = ByteArrayOutputStream()
        for (quality in 100 downTo 1 step 10) {
            val jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next()
            jpgWriter.output = ImageIO.createImageOutputStream(out)

            val jpgWriteParam = jpgWriter.defaultWriteParam
            jpgWriteParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
            jpgWriteParam.compressionQuality = quality / 100f

            val outputImage = IIOImage(image, null, null)
            jpgWriter.write(null, outputImage, jpgWriteParam)

            jpgWriter.dispose()
            if (out.size() <= MAX_IMAGE_SIZE) {
                LOG.info { "Compressed image to ${out.size()} bytes, quality $quality" }
                return ByteArrayInputStream(out.toByteArray())
            }
            out.reset()
        }
        throw IOException()
    }
}
