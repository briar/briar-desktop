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

import org.briarproject.briar.api.privategroup.GroupMessageHeader
import org.briarproject.briar.desktop.threadedgroup.conversation.ThreadItem
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class PrivateGroupMessageItem(h: GroupMessageHeader, text: String) : ThreadItem(
    messageId = h.id,
    parentId = h.parentId,
    text = text,
    timestamp = h.timestamp,
    author = h.author,
    authorInfo = h.authorInfo,
    isRead = h.isRead
)
