package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.desktop.contact.MessageCounter
import org.briarproject.briar.desktop.theme.ChevronDown
import org.briarproject.briar.desktop.theme.ChevronUp
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.replaceIfIndexed
import java.time.Instant

fun main() = preview(
    "num_messages" to 20,
    "first_unread_index" to 5
) {
    val numMessages = getIntParameter("num_messages")
    val initialFirstUnreadIndex = getIntParameter("first_unread_index")

    // re-create messages and currentUnreadMessageInfo as soon as numMessages or initialFirstUnreadIndex change
    val loading by produceState(true, numMessages, initialFirstUnreadIndex) { value = true; delay(500); value = false }

    val messages = remember(loading) {
        mutableStateListOf<ConversationItem>().apply {
            addAll(
                (0 until numMessages).map { idx ->
                    ConversationMessageItem(
                        text = "Example Text $idx",
                        id = MessageId(getRandomId()),
                        groupId = GroupId(getRandomId()),
                        time = Instant.now().minusSeconds((numMessages - idx).toLong() * 60).toEpochMilli(),
                        autoDeleteTimer = 0,
                        isIncoming = idx % 2 == 0,
                        isRead = idx % 2 == 1 || idx < initialFirstUnreadIndex,
                        isSent = false,
                        isSeen = false
                    )
                }
            )
        }
    }

    val currentUnreadMessagesInfo by remember(loading) {
        derivedStateOf {
            ConversationViewModel.UnreadMessagesInfo(
                amount = messages.count { !it.isRead },
                firstIndex = messages.indexOfFirst { !it.isRead }
            )
        }
    }

    if (loading) {
        Loader()
        return@preview
    }

    ConversationList(
        padding = PaddingValues(0.dp),
        messages = messages,
        initialFirstUnreadMessageIndex = initialFirstUnreadIndex,
        currentUnreadMessagesInfo = currentUnreadMessagesInfo,
        markMessagesRead = { lst ->
            messages.replaceIfIndexed(
                { idx, it -> idx in lst && !it.isRead },
                { _, it -> it.markRead() }
            )
        },
        respondToRequest = { _, _ -> },
        deleteMessage = {}
    )
}

@Composable
fun ConversationList(
    padding: PaddingValues,
    messages: List<ConversationItem>,
    initialFirstUnreadMessageIndex: Int,
    currentUnreadMessagesInfo: ConversationViewModel.UnreadMessagesInfo,
    markMessagesRead: (List<Int>) -> Unit,
    respondToRequest: (ConversationRequestItem, Boolean) -> Unit,
    deleteMessage: (MessageId) -> Unit,
) {
    // we need to make sure the ConversationList is out of composition before showing new messages
    // so that the list state and the coroutine scope is created anew
    // this is currently assured by (briefly) showing a loader while loading the messages
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState(
        if (initialFirstUnreadMessageIndex != -1) initialFirstUnreadMessageIndex
        else if (messages.isNotEmpty()) messages.lastIndex
        else 0
    )

    Box(modifier = Modifier.padding(padding).fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
            modifier = Modifier.fillMaxSize().padding(end = 12.dp)
        ) {
            itemsIndexed(messages) { idx, m ->
                if (idx == initialFirstUnreadMessageIndex) {
                    UnreadMessagesMarker()
                }
                when (m) {
                    is ConversationMessageItem -> ConversationMessageItemView(m)
                    is ConversationNoticeItem -> ConversationNoticeItemView(m)
                    is ConversationRequestItem ->
                        ConversationRequestItemView(
                            m,
                            onResponse = { accept -> respondToRequest(m, accept) },
                        )
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )

        if (currentUnreadMessagesInfo.amount > 0) {
            val delayUntilMarkedAsRead = 500L
            LaunchedEffect(currentUnreadMessagesInfo, scrollState.firstVisibleItemIndex) {
                // mark all messages visible on the screen for more than [delayUntilMarkedAsRead] milliseconds as read
                delay(delayUntilMarkedAsRead)
                markMessagesRead(scrollState.layoutInfo.visibleItemsInfo.map { it.index })
            }
            val showUnreadButton by produceState(false) {
                // never show FAB before all messages currently visible are marked as read
                delay(delayUntilMarkedAsRead + 100)
                value = true
            }

            if (showUnreadButton) {
                UnreadMessagesFAB(
                    arrowUp = currentUnreadMessagesInfo.firstIndex < scrollState.firstVisibleItemIndex,
                    counter = currentUnreadMessagesInfo.amount,
                    onClick = {
                        scope.launch {
                            scrollState.animateScrollToItem(currentUnreadMessagesInfo.firstIndex, 0)
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp) // todo: check padding
                )
            }
        }
    }
}

@Composable
fun UnreadMessagesMarker() = Box {
    HorizontalDivider(Modifier.align(Alignment.Center))
    Text(
        text = i18n("conversation.message.unread"),
        modifier = Modifier
            .align(Alignment.Center)
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colors.divider, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.background)
            .padding(8.dp)
    )
}

@Composable
fun UnreadMessagesFAB(
    arrowUp: Boolean,
    counter: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    FloatingActionButton(onClick) {
        val arrow = if (arrowUp)
            Icons.Filled.ChevronUp else Icons.Filled.ChevronDown
        Icon(arrow, i18n("access.message.jump_to_unread"))
    }
    MessageCounter(
        counter,
        Modifier.align(Alignment.TopEnd).offset(3.dp, (-3).dp)
    )
}
