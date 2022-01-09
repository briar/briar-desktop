package org.briarproject.briar.desktop.utils

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import mu.KotlinLogging
import java.io.FileInputStream

object ImagePicker {

    private val LOG = KotlinLogging.logger {}

    private val SUPPORTED_EXTENSIONS = setOf("png", "jpg", "jpeg")

    fun pickImageUsingDialog(window: ComposeWindow, updateImage: (ImageBitmap?) -> Unit) {
        val dialog = java.awt.FileDialog(window)
        dialog.isMultipleMode = false
        dialog.setFilenameFilter { dir, name ->
            val parts = name.split(".")
            if (parts.size < 2) {
                false
            } else {
                val extension = parts[parts.size - 1]
                SUPPORTED_EXTENSIONS.contains(extension.lowercase())
            }
        }
        dialog.isVisible = true
        val files = dialog.files
        val file = if (files == null || files.isEmpty()) null else files[0]
        LOG.debug { "Loading image from file '$file'" }
        if (file == null) {
            updateImage(null)
        } else {
            val image = try {
                FileInputStream(file).use {
                    loadImageBitmap(it)
                }
            } catch (e: Throwable) {
                LOG.warn(e) { "Error while loading image" }
                null
            }
            updateImage(image)
        }
    }
}
