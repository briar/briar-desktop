package org.briarproject.briar.desktop.chat;

import org.briarproject.briar.api.conversation.ConversationMessageHeader;

import java.util.Comparator;

public class ConversationMessageHeaderComparator
        implements Comparator<ConversationMessageHeader> {

    @Override
    public int compare(ConversationMessageHeader h1,
                       ConversationMessageHeader h2) {
        return Long.compare(h1.getTimestamp(), h2.getTimestamp());
    }

}
