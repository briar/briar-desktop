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

package org.briarproject.briar.desktop.utils

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.ImageBitmap
import io.github.oshai.kotlinlogging.KotlinLogging
import org.briarproject.briar.desktop.utils.ImageUtils.loadImageBitmap
import org.briarproject.briar.desktop.utils.KLoggerUtils.d
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
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
        LOG.d { "Loading image from file '$file'" }
        if (file == null) {
            updateImage(null)
        } else {
            val image = try {
                FileInputStream(file).use {
                    loadImageBitmap(it)
                }
            } catch (e: Throwable) {
                LOG.w(e) { "Error while loading image" }
                null
            }
            updateImage(image)
        }
    }
}
