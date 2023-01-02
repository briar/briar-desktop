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

package org.briarproject.briar.desktop.testdata.conversation

import org.briarproject.briar.desktop.testdata.contact.Contact
import org.briarproject.briar.desktop.testdata.conversation.Direction.INCOMING
import org.briarproject.briar.desktop.testdata.conversation.Direction.OUTGOING
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DslMarker
annotation class ConversationsDsl

fun conversations(block: ConversationsBuilder.() -> Unit): Conversations = ConversationsBuilder().apply(block).build()

@ConversationsDsl
class ConversationsBuilder {

    private val conversations = mutableMapOf<Contact, Conversation>()

    fun conversation(contact: Contact, block: ConversationBuilder.() -> Unit) {
        check(!conversations.contains(contact)) { "A contact cannot be linked to two conversations." } // NON-NLS
        conversations[contact] = ConversationBuilder().apply(block).build()
    }

    fun build(): Conversations = conversations
}

@ConversationsDsl
class ConversationBuilder {

    private val messages = mutableListOf<Message>()

    fun incoming(block: MessageBuilder.() -> Unit) {
        messages.add(MessageBuilder(INCOMING).apply(block).build())
    }

    fun outgoing(block: MessageBuilder.() -> Unit) {
        messages.add(MessageBuilder(OUTGOING).apply(block).build())
    }

    fun build(): Conversation {
        return Conversation(messages)
    }
}

var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@ConversationsDsl
class MessageBuilder(private val direction: Direction) {

    var text: String? = null
    var images: List<String> = emptyList()
    var read: Boolean = false

    private var sent: LocalDateTime = LocalDateTime.now()
    var date: Any = ""
        set(value) {
            if (value is String) {
                sent = LocalDateTime.parse(value, formatter)
            } else if (value is LocalDateTime) {
                sent = value
            }
        }

    fun build(): Message = Message(text, images, direction, sent, read)
}
