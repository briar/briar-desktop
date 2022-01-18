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

import java.time.LocalDateTime

data class Conversations(
    val persons: List<Conversation>
)

data class Conversation(
    val name: String,
    var messages: List<Message>
)

data class Message(
    val text: String,
    val images: List<String>,
    val direction: Direction,
    val date: LocalDateTime,
    val read: Boolean
)

enum class Direction {
    INCOMING,
    OUTGOING
}
