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

package org.briarproject.briar.desktop.testdata.forum

import org.briarproject.briar.desktop.testdata.contact.Contact
import java.time.LocalDateTime

typealias Forums = List<Forum>

data class Forum(
    val name: String,
    val members: List<PostAuthor>,
    val posts: List<Post>,
)

data class Post(
    val author: PostAuthor,
    val text: String,
    val date: LocalDateTime,
    val replies: List<Post>,
)

sealed interface PostAuthor {

    val name: String

    object Me : PostAuthor {
        override val name: String = "" // todo: real name
    }

    data class ContactAuthor(
        val contact: Contact,
        val sharedWith: Boolean,
    ) : PostAuthor {
        override val name: String = contact.name
    }

    data class StrangerAuthor(
        override val name: String,
    ) : PostAuthor
}
