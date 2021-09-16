package org.briarproject.briar.desktop.chat

import org.briarproject.briar.api.conversation.ConversationMessageHeader

class ConversationMessageHeaderComparator : Comparator<ConversationMessageHeader> {

    override fun compare(
        h1: ConversationMessageHeader,
        h2: ConversationMessageHeader
    ): Int {
        return h1.timestamp.compareTo(h2.timestamp)
    }
}
