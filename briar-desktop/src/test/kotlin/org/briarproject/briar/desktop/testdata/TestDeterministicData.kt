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

package org.briarproject.briar.desktop.testdata

import org.briarproject.briar.desktop.testdata.conversation.conversations
import org.briarproject.briar.desktop.testdata.forum.Post
import org.briarproject.briar.desktop.testdata.forum.PostAuthor
import org.briarproject.briar.desktop.testdata.forum.forums

fun main() {
    for (conversation in conversations.persons) {
        println("conversation with: ${conversation.name}") // NON-NLS
        for (message in conversation.messages) {
            println("  ${message.direction} ${message.text} ${message.read} ${message.date}")
        }
    }

    for (forum in forums.forums) {
        println("Forum: ${forum.name}") // NON-NLS
        fun printPost(post: Post, level: Int) {
            val name = if (post.author is PostAuthor.RemoteAuthor) post.author.name else "me"
            println("|".repeat(level) + "+ ${post.date} - $name: ${post.text}")
            post.replies.forEach { printPost(it, level + 1) }
        }
        forum.posts.forEach { printPost(it, 0) }
    }
}
