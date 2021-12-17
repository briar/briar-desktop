package org.briarproject.briar.desktop.testdata

import org.briarproject.briar.api.test.TestAvatarCreator
import org.briarproject.briar.desktop.attachment.media.ImageCompressor
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.InputStream
import java.lang.Integer.max
import javax.inject.Inject
import kotlin.random.Random

class TestAvatarCreatorImpl @Inject internal constructor(private val imageCompressor: ImageCompressor) :
    TestAvatarCreator {

    private val WIDTH = 800
    private val HEIGHT = 640

    private val random = Random(0)

    override fun getAvatarInputStream(): InputStream {
        val image = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)

        if (random.nextBoolean()) {
            generateColoredPixels(image)
        } else {
            generateColoredCircles(image)
        }

        return imageCompressor.compressImage(image)
    }

    private fun generateColoredPixels(image: BufferedImage) {
        val g: Graphics2D = image.createGraphics()
        val pixelMultiplier: Int = random.nextInt(500) + 1

        for (x in 0..WIDTH step pixelMultiplier) {
            for (y in 0..HEIGHT step pixelMultiplier) {
                g.color = Color(getRandomColor())
                g.fillRect(x, y, pixelMultiplier, pixelMultiplier)
            }
        }
    }

    private fun generateColoredCircles(image: BufferedImage) {
        val g: Graphics2D = image.createGraphics()

        g.color = Color.WHITE
        g.fillRect(0, 0, image.width, image.height)

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val biggestSide = max(WIDTH, HEIGHT)
        val selectedCount = random.nextInt(10) + 2
        val radiusFrom = biggestSide / 12f
        val radiusTo = biggestSide / 4f
        for (i in 0..selectedCount) {
            val cx = random.nextInt(WIDTH)
            val cy = random.nextInt(HEIGHT)
            val radius = (random.nextInt((radiusTo - radiusFrom).toInt()) + radiusFrom).toInt()
            val diameter = radius * 2
            g.color = Color(getRandomColor())
            g.fillOval(cx - radius, cy - radius, diameter, diameter)
        }
    }

    private fun getRandomColor(): Int {
        val hue = random.nextFloat()
        val saturation = random.nextFloat()
        val brightness = 1f
        return Color.HSBtoRGB(hue, saturation, brightness)
    }
}
