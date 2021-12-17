package org.briarproject.briar.desktop.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.identity.AuthorManager

object ImageUtils {

    fun loadAvatar(
        authorManager: AuthorManager,
        attachmentReader: AttachmentReader,
        txn: Transaction,
        contact: Contact
    ): ImageBitmap? {
        val authorInfo = authorManager.getAuthorInfo(txn, contact)
        if (authorInfo.avatarHeader == null) {
            return null
        }
        val attachment = attachmentReader.getAttachment(txn, authorInfo.avatarHeader)
        attachment.stream.use {
            return loadImageBitmap(it)
        }
    }
}
