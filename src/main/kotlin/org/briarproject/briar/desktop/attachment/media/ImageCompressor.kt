package org.briarproject.briar.desktop.attachment.media

import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream

interface ImageCompressor {

    /**
     * Compress an image and return an InputStream from which the resulting
     * image can be read. The image will be compressed as a JPEG image such that
     * it fits into a message.
     *
     * @param image the source image
     * @return a stream from which the resulting image can be read
     */
    @Throws(IOException::class)
    fun compressImage(image: BufferedImage): InputStream
}
