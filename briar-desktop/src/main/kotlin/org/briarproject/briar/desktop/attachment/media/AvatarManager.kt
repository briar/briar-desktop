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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.attachment.AttachmentHeader
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.ui.LocalAvatarManager
import javax.inject.Inject

class AvatarManager @Inject constructor(
    private val attachmentReader: AttachmentReader,
    private val executors: BriarExecutors,
) {

    // access only on Dispatchers.Swing
    // TODO we may want to monitor cache size and evict cache entries again
    private val cache = HashMap<MessageId, ImageBitmap>()

    suspend fun loadAvatar(
        attachmentHeader: AttachmentHeader,
    ): ImageBitmap = withContext(Dispatchers.Swing) {
        val imageBitmap = cache[attachmentHeader.messageId]
        if (imageBitmap != null) return@withContext imageBitmap
        executors.runOnDbThreadWithTransaction(true) { txn ->
            attachmentReader.getAttachment(txn, attachmentHeader).stream.use { inputStream ->
                loadImageBitmap(inputStream)
            }
        }.also { cache[attachmentHeader.messageId] = it }
    }
}

/**
 * Produces a state with an [ImageBitmap] representing the avatar of the given [authorInfo].
 * While loading, the value of the state is null.
 * When no state, but null is returned, the given [authorInfo] has no avatar.
 */
@Composable
fun AvatarProducer(authorInfo: AuthorInfo): State<ImageBitmap?>? {
    val avatarHeader = authorInfo.avatarHeader
    return if (avatarHeader == null) {
        null
    } else {
        val avatarManager = checkNotNull(LocalAvatarManager.current)
        produceState<ImageBitmap?>(null, avatarHeader.messageId) {
            value = avatarManager.loadAvatar(avatarHeader)
        }
    }
}
