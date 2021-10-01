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
    val direction: Direction,
    val date: LocalDateTime,
    val read: Boolean
)

enum class Direction {
    INCOMING,
    OUTGOING
}
