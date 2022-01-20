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

import java.net.URL
import javax.imageio.ImageIO
import kotlin.test.Test

class ImageCompressorTest {

    private val compressor = ImageCompressorImpl()

    @Test
    fun `can compress voronoi image`() {
        // load image
        val input = Thread.currentThread().contextClassLoader.getResourceAsStream("images/voronoi1.png")
        val image = input.use {
            ImageIO.read(input)
        }
        println("image size: ${image.width}x${image.height}")

        // compress image
        val compressed = compressor.compressImage(image)

        // reload compressed image
        val reloaded = compressed.use {
            ImageIO.read(compressed)
        }
        println("image size: ${reloaded.width}x${reloaded.height}")
    }

    @Test
    fun `can compress quasar`() {
        // load image
        val url = "https://upload.wikimedia.org/wikipedia/commons/3/38/Artist%27s_rendering_ULAS_J1120%2B0641.jpg"
        val input = URL(url).openStream()
        val image = input.use {
            ImageIO.read(input)
        }
        println("image size: ${image.width}x${image.height}")

        // compress image
        val compressed = compressor.compressImage(image)

        // reload compressed image
        val reloaded = compressed.use {
            ImageIO.read(compressed)
        }
        println("image size: ${reloaded.width}x${reloaded.height}")
    }

}
