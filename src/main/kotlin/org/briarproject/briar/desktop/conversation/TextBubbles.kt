package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.UiState

@Composable
fun TextBubbles(chat: UiState<Chat>) {
    when (chat) {
        is UiState.Loading -> Loader()
        is UiState.Error -> Loader()
        is UiState.Success ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(8.dp)
            ) {
                items(chat.data.messages) { m -> TextBubble(m) }
            }
    }
}
