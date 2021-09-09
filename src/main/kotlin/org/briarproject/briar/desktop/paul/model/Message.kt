package org.briarproject.briar.desktop.paul.model

data class Message(
    val from: String?,
    val message: String,
    val time: String,
    val delivered: Boolean,
    //val read: Boolean,
)