/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.attachment.media

import mu.KotlinLogging
import org.briarproject.briar.api.attachment.MediaConstants.MAX_IMAGE_SIZE
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.inject.Inject
import kotlin.math.max

class ImageCompressorImpl @Inject internal constructor() : ImageCompressor {

    companion object {
        val LOG = KotlinLogging.logger {}

        const val MAX_ATTACHMENT_DIMENSION = 1000
    }

    override fun compressImage(image: BufferedImage): InputStream {
        val out = ByteArrayOutputStream()

        // First make sure we're dealing with an image without alpha channel. Alpha channels are not supported by JPEG
        // compression later on. If the image contains an alpha channel, we draw it onto an image without alpha channel.
        val withoutAlpha = if (!image.colorModel.hasAlpha()) image else {
            val replacement = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
            replacement.apply { createGraphics().drawImage(image, 0, 0, null) }
        }

        // Now determine the factor by which we scale down the image in order to end up with an image
        // with both sides smaller than [MAX_ATTACHMENT_DIMENSION].
        val maxSize = max(withoutAlpha.width, withoutAlpha.height)
        var factor = 1
        while (maxSize / factor > MAX_ATTACHMENT_DIMENSION) {
            factor *= 2
        }

        // Now, if we determined a factor greater 1 reduce image dimensions
        val scaled = if (factor != 1) scaleDown(withoutAlpha, factor) else withoutAlpha

        // After that, compress image. Try with maximum quality and reduce until we can compress below
        // a size of [MAX_IMAGE_SIZE]. We try quality levels 100, 90, ..., 20, 10.
        for (quality in 100 downTo 1 step 10) {
            val jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next()
            jpgWriter.output = ImageIO.createImageOutputStream(out)

            val jpgWriteParam = jpgWriter.defaultWriteParam
            jpgWriteParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
            jpgWriteParam.compressionQuality = quality / 100f

            val outputImage = IIOImage(scaled, null, null)
            jpgWriter.write(null, outputImage, jpgWriteParam)

            jpgWriter.dispose()
            if (out.size() <= MAX_IMAGE_SIZE) {
                LOG.i { "Compressed image to ${out.size()} bytes, quality $quality" }
                return ByteArrayInputStream(out.toByteArray())
            }
            out.reset()
        }
        throw IOException()
    }

    private fun scaleDown(image: BufferedImage, factor: Int): BufferedImage {
        // Calculate new images dimensions
        val w = image.width / factor
        val h = image.height / factor
        // Determine vertical and horizontal scale factors. We accept some minimal distortion here as it is quite
        // possible that the integer division above led to different effective scale factors. Since we need integral
        // dimensions there's not much we could do about that.
        val sx = w / image.width.toDouble()
        val sy = h / image.height.toDouble()
        // Create new image of same type and scale down
        var resized = BufferedImage(w, h, image.type)
        val at = AffineTransform().also {
            it.scale(sx, sy)
        }
        val scale = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        return scale.filter(image, resized)
    }
}
