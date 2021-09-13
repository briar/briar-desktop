package org.briarproject.briar.desktop.paul.model

data class Contact(
    val name: String,
    val online: Boolean,
    val profile_pic: String,
    val last_heard: String,
    val privateMessages: List<Message>
)
