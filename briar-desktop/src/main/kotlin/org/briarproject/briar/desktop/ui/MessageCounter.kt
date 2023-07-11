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

package org.briarproject.briar.desktop.ui

interface MessageCounter {

    fun addListener(listener: MessageCounterListener): Boolean

    fun removeListener(listener: MessageCounterListener): Boolean
}

enum class MessageCounterDataType { PrivateMessage, Forum, PrivateGroup, Blog }

/**
 * Data holder for MessageCounter updates.
 */
data class MessageCounterData(
    /**
     * Type of unread messages.
     */
    val type: MessageCounterDataType,
    /**
     * Sum of all unread messages of the given [type].
     */
    val total: Int,
    /**
     * Amount of different private chats/groups/forums (depending on [type]) with unread messages.
     */
    val groups: Int,
    /**
     * If `true`, [total] has increased since the last time the listeners were informed.
     */
    val increment: Boolean,
)

typealias MessageCounterListener = (MessageCounterData) -> Unit
