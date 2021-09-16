package org.briarproject.briar.desktop.chat

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Chat {

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    val messages: MutableList<SimpleMessage> = ArrayList()

    fun add(message: SimpleMessage) {
        messages.add(message)
    }

    fun appendMessage(local: Boolean, timestamp: Long, messageText: String?) {
        val name = if (local) "You" else "Other"
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()
        )
        val author = String.format("%s (%s): ", name, formatter.format(dateTime))
        messages.add(SimpleMessage(local, author, messageText!!, "time", true))
    }
}
