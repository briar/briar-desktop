package org.briarproject.briar.desktop.conversation

import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.event.Event
import org.briarproject.briar.api.conversation.ConversationMessageHeader

/**
 * An event that is broadcast when a new conversation message to be sent is added.
 */
data class ConversationMessageToBeSentEvent(
    val messageHeader: ConversationMessageHeader,
    val contactId: ContactId
) : Event()
