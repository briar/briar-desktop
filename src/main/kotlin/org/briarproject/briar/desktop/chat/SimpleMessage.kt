package org.briarproject.briar.desktop.chat

data class SimpleMessage(
    val local: Boolean,
    val from: String?,
    val message: String,
    val time: String,
    val delivered: Boolean,
    // val read: Boolean,
)
