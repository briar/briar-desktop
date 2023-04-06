/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

package org.briarproject.briar.desktop.privategroup.conversation

import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.api.privategroup.GroupMessageHeader
import org.briarproject.briar.api.privategroup.JoinMessageHeader
import org.briarproject.briar.desktop.threadedgroup.conversation.ThreadItem
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
open class PrivateGroupMessageItem(h: GroupMessageHeader, text: String) : ThreadItem(
    messageId = h.id,
    parentId = h.parentId,
    text = text,
    timestamp = h.timestamp,
    author = h.author,
    authorInfo = h.authorInfo,
    isRead = h.isRead
)

class PrivateGroupJoinItem(h: JoinMessageHeader) : PrivateGroupMessageItem(h, "") {
    val isInitial = h.isInitial
}

val ThreadItem.isMeta: Boolean get() = this is PrivateGroupJoinItem

val ThreadItem.metaText: String
    get() {
        if (this !is PrivateGroupJoinItem) throw IllegalArgumentException()
        return if (isInitial) {
            if (authorInfo.status == OURSELVES) i18n("group.meta.created.you")
            else i18nF("group.meta.created.other", authorName)
        } else {
            if (authorInfo.status == OURSELVES) i18n("group.meta.joined.you")
            else i18nF("group.meta.joined.other", authorName)
        }
    }
