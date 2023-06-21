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

package org.briarproject.briar.desktop.blog

import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.blog.BlogCommentHeader
import org.briarproject.briar.api.blog.BlogPostHeader
import org.briarproject.briar.api.identity.AuthorInfo

sealed class BlogPost(
    open val header: BlogPostHeader,
    open val text: String?,
) : Comparable<BlogPostItem> {
    abstract val postHeader: BlogPostHeader
    val isRead: Boolean get() = header.isRead
    val id: MessageId get() = header.id
    val groupId: GroupId get() = header.groupId
    val timestamp: Long get() = header.timestamp
    val author: Author get() = header.author
    val authorInfo: AuthorInfo get() = header.authorInfo
    val isRssFeed: Boolean get() = header.isRssFeed

    override operator fun compareTo(other: BlogPostItem): Int {
        return if (this === other) 0 else other.header.timeReceived.compareTo(header.timeReceived)
    }
}

data class BlogPostItem(
    override val header: BlogPostHeader,
    override val text: String,
) : BlogPost(header, text) {
    override val postHeader: BlogPostHeader get() = header
}

data class BlogCommentItem(
    override val header: BlogCommentHeader,
    override val postHeader: BlogPostHeader,
    override val text: String?,
) : BlogPost(header, text) {

    companion object {
        fun getBlogPostHeader(header: BlogPostHeader): BlogPostHeader {
            return if (header is BlogCommentHeader) {
                getBlogPostHeader(header.parent)
            } else {
                header
            }
        }
    }

    private val _comments = ArrayList<BlogCommentHeader>()
    val comments: List<BlogCommentHeader> get() = _comments

    init {
        collectComments(header)
        // TODO check order
        _comments.sortBy { it.timestamp }
    }

    private fun collectComments(header: BlogPostHeader): BlogPostHeader {
        return if (header is BlogCommentHeader) {
            if (header.comment != null) _comments.add(header)
            collectComments(header.parent)
        } else {
            header
        }
    }
}
