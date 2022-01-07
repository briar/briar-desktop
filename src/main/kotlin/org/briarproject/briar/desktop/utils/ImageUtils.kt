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
        return loadAvatar(txn, attachmentReader, avatarHeader)
    }

    fun loadAvatar(
        txn: Transaction,
        attachmentReader: AttachmentReader,
        attachmentHeader: AttachmentHeader,
    ): ImageBitmap? {
        val attachment = attachmentReader.getAttachment(txn, attachmentHeader)
        attachment.stream.use {
            return loadImageBitmap(it)
        }
    }
}
