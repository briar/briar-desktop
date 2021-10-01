package org.briarproject.briar.desktop.testdata

import org.briarproject.briar.desktop.testdata.Direction.INCOMING
import org.briarproject.briar.desktop.testdata.Direction.OUTGOING
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DslMarker
annotation class ConversationsDsl

fun conversations(block: ConversationsBuilder.() -> Unit): Conversations = ConversationsBuilder().apply(block).build()

@ConversationsDsl
class ConversationsBuilder {

    private val conversations = mutableListOf<Conversation>()

    fun conversation(block: ConversationBuilder.() -> Unit) {
        conversations.add(ConversationBuilder().apply(block).build())
    }

    fun build(): Conversations = Conversations(conversations)
}

@ConversationsDsl
class ConversationBuilder {

    var contactName: String = ""

    private val messages = mutableListOf<Message>()

    fun incoming(block: MessageBuilder.() -> Unit) {
        messages.add(MessageBuilder(INCOMING).apply(block).build())
    }

    fun outgoing(block: MessageBuilder.() -> Unit) {
        messages.add(MessageBuilder(OUTGOING).apply(block).build())
    }

    fun build(): Conversation = Conversation(contactName, messages)
}

var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@ConversationsDsl
class MessageBuilder(private val direction: Direction) {

    var text: String = ""
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

    fun build(): Message = Message(text, direction, sent, read)
}
