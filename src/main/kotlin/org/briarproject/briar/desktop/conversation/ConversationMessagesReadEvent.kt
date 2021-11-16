package org.briarproject.briar.desktop.conversation

import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.event.Event

/**
 * An event that is broadcast when conversation messages
 * are shown on the screen for the first time.
 */
data class ConversationMessagesReadEvent(
    val count: Int,
    val contactId: ContactId
) : Event()
