package org.briarproject.briar.desktop.conversation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.briar.api.conversation.ConversationMessageHeader
import org.briarproject.briar.desktop.CVM
import org.briarproject.briar.desktop.MM
import org.briarproject.briar.desktop.ui.UiState
import java.util.Collections

@Composable
fun ChatState(id: ContactId): MutableState<UiState<Chat>> {
    val state: MutableState<UiState<Chat>> = remember { mutableStateOf(UiState.Loading) }
    val messagingManager = MM.current
    val conversationManager = CVM.current

    DisposableEffect(id) {
        state.value = UiState.Loading
        val chat = Chat()
        val visitor = ChatHistoryConversationVisitor(chat, messagingManager)
        val messageHeaders: List<ConversationMessageHeader> = ArrayList(conversationManager.getMessageHeaders(id))
        Collections.sort(messageHeaders, ConversationMessageHeaderComparator())
        // Reverse order here because we're using reverseLayout=true on the LazyColumn to display items
        // from bottom to top
        Collections.reverse(messageHeaders)
        for (header in messageHeaders) {
            header.accept(visitor)
        }
        state.value = UiState.Success(chat)
        onDispose { }
    }
    return state
}
