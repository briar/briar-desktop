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
