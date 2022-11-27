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

import java.time.LocalDateTime

data class Forums(
    val forums: List<Forum>
)

data class Forum(
    val name: String,
    var members: List<PostAuthor>,
    var posts: List<Post>,
)

data class Post(
    val author: PostAuthor,
    val text: String,
    val date: LocalDateTime,
    val replies: List<Post>,
)

sealed interface PostAuthor {
    object Me : PostAuthor

    data class RemoteAuthor(
        val name: String,
        val sharedWith: Boolean, // todo: not supported yet
    ) : PostAuthor
}
