package org.briarproject.briar.desktop.privategroups

import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.desktop.viewmodel.BriarEventListenerViewModel
import java.util.logging.Logger
import javax.inject.Inject

class ThreadedConversationViewModel
@Inject
constructor(
    private val privateGroupManager: PrivateGroupManager,
    private val conversationManager: ConversationManager,
    private val messagingManager: MessagingManager,
    private val privateMessageFactory: PrivateMessageFactory,
    private val eventBus: EventBus,
) : BriarEventListenerViewModel(eventBus) {

    companion object {
        private val LOG = Logger.getLogger(ThreadedConversationViewModel::class.java.name)
    }

    override fun eventOccurred(e: Event?) {
        // TODO
    }
}
