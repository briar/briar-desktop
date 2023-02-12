/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.forums

import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.client.MessageTreeImpl
import org.briarproject.briar.desktop.group.conversation.ThreadItem
import org.briarproject.briar.desktop.threading.UiExecutor

sealed class PostsState
object Loading : PostsState()
class Loaded(
    val messageTree: MessageTreeImpl<ThreadItem>,
    val scrollTo: MessageId? = null,
) : PostsState() {
    val posts: List<ThreadItem> = messageTree.depthFirstOrder()

    @UiExecutor
    fun unreadBeforeIndex(index: Int): UnreadPostInfo {
        if (index <= 0 || index >= posts.size) return UnreadPostInfo(null, 0)

        var lastUnread: Int? = null
        var num = 0
        for (i in 0 until index) if (!posts[i].isRead) {
            lastUnread = i
            num++
        }
        return UnreadPostInfo(lastUnread, num)
    }

    @UiExecutor
    fun unreadAfterIndex(index: Int): UnreadPostInfo {
        if (index < 0 || index >= posts.size) return UnreadPostInfo(null, 0)

        var firstUnread: Int? = null
        var num = 0
        for (i in index + 1 until posts.size) {
            if (!posts[i].isRead) {
                if (firstUnread == null) firstUnread = i
                num++
            }
        }
        return UnreadPostInfo(firstUnread, num)
    }
}

class UnreadPostInfo(
    val nextUnreadIndex: Int?,
    val numUnread: Int,
)
