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

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.briar.api.attachment.AttachmentHeader
import org.briarproject.briar.api.attachment.AttachmentReader
import java.io.InputStream

object ImageUtils {

    fun loadImage(
        txn: Transaction,
        attachmentReader: AttachmentReader,
        attachmentHeader: AttachmentHeader,
    ): ImageBitmap {
        val attachment = attachmentReader.getAttachment(txn, attachmentHeader)
        attachment.stream.use {
            return loadImageBitmap(it)
        }
    }

    fun loadImageBitmap(it: InputStream): ImageBitmap {
        return org.jetbrains.skia.Image.makeFromEncoded(it.readAllBytes()).toComposeImageBitmap()
    }
}
