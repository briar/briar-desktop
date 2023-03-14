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

package org.briarproject.briar.desktop.threadedgroup.conversation

import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.client.MessageTree
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import javax.annotation.concurrent.NotThreadSafe

// TODO the mutable state here might need to be made immutable later
//  and copy on data classes used to trigger recompositions when items update
@NotThreadSafe
abstract class ThreadItem(
    private val messageId: MessageId,
    private val parentId: MessageId?,
    val text: String,
    private val timestamp: Long,
    val author: Author,
    val authorInfo: AuthorInfo,
    var isRead: Boolean,
) : MessageTree.MessageNode {

    companion object {
        const val UNDEFINED = -1
    }

    private var level: Int = UNDEFINED
    var isHighlighted = false

    override fun getId(): MessageId = messageId
    override fun getParentId(): MessageId? = parentId
    override fun getTimestamp(): Long = timestamp
    fun getLevel(): Int = level

    /**
     * Returns the author's name, with an alias if one exists.
     */
    val authorName: String
        get() = getContactDisplayName(author.name, authorInfo.alias)

    override fun setLevel(level: Int) {
        this.level = level
    }

    override fun hashCode(): Int = messageId.hashCode()

    override fun equals(other: Any?): Boolean {
        return other is ThreadItem && messageId == other.messageId
    }
}
