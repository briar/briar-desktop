package org.briarproject.briar.desktop.contact

sealed interface ContactItem {

    val contactId: Any
    val displayName: String
    val isConnected: Boolean
    val isEmpty: Boolean
    val unread: Int
    val timestamp: Long
}
