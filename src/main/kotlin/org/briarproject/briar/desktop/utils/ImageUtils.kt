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
import androidx.compose.ui.res.loadImageBitmap
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.briar.api.attachment.AttachmentHeader
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.identity.AuthorManager

object ImageUtils {

    fun loadAvatar(
        authorManager: AuthorManager,
        attachmentReader: AttachmentReader,
        txn: Transaction,
        contact: Contact,
    ): ImageBitmap? {
        val authorInfo = authorManager.getAuthorInfo(txn, contact)
        val avatarHeader = authorInfo.avatarHeader ?: return null
        return loadImage(txn, attachmentReader, avatarHeader)
    }

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
}
