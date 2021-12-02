package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.client.SessionId
import org.briarproject.briar.desktop.conversation.ConversationRequestItem.RequestType.INTRODUCTION
import org.briarproject.briar.desktop.theme.msgIn
import org.briarproject.briar.desktop.theme.msgOut
import org.briarproject.briar.desktop.theme.msgStroke
import org.briarproject.briar.desktop.theme.privateMessageDate
import org.briarproject.briar.desktop.theme.textPrimary
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import java.time.Instant

fun main() = preview {
    LazyColumn {
        item {
            ConversationNoticeItemView(
                ConversationNoticeItem(
                    msgText = "Let's test a received notice message.",
                    text = "Text of notice message.",
                    id = MessageId(getRandomId()),
                    groupId = GroupId(getRandomId()),
                    time = Instant.now().toEpochMilli(),
                    autoDeleteTimer = 0,
                    isIncoming = true,
                    isRead = true,
                    isSent = false,
                    isSeen = false,
                )
            )
        }
        item {
            ConversationMessageItemView(
                ConversationMessageItem(
                    text = "This is a medium-sized message that has been sent before receiving the request message.",
                    id = MessageId(getRandomId()),
                    groupId = GroupId(getRandomId()),
                    time = Instant.now().toEpochMilli(),
                    autoDeleteTimer = 0,
                    isIncoming = false,
                    isRead = false,
                    isSent = true,
                    isSeen = true,
                )
            )
        }
        item {
            ConversationRequestItemView(
                ConversationRequestItem(
                    requestedGroupId = null,
                    requestType = INTRODUCTION,
                    sessionId = SessionId(getRandomId()),
                    answered = false,
                    msgText = "Short message.",
                    text = "Text of notice message.",
                    id = MessageId(getRandomId()),
                    groupId = GroupId(getRandomId()),
                    time = Instant.now().toEpochMilli(),
                    autoDeleteTimer = 0,
                    isIncoming = true,
                    isRead = true,
                    isSent = false,
                    isSeen = false,
                )
            )
        }
        item {
            ConversationNoticeItemView(
                ConversationNoticeItem(
                    msgText = "This is a long long long message that spans over several lines.\n\nIt ends here.",
                    text = "Text of notice message.",
                    id = MessageId(getRandomId()),
                    groupId = GroupId(getRandomId()),
                    time = Instant.now().toEpochMilli(),
                    autoDeleteTimer = 0,
                    isIncoming = false,
                    isRead = false,
                    isSent = true,
                    isSeen = true,
                )
            )
        }
        item {
            ConversationMessageItemView(
                ConversationMessageItem(
                    text = "Just also receiving a normal message.",
                    id = MessageId(getRandomId()),
                    groupId = GroupId(getRandomId()),
                    time = Instant.now().toEpochMilli(),
                    autoDeleteTimer = 0,
                    isIncoming = true,
                    isRead = true,
                    isSent = false,
                    isSeen = false,
                )
            )
        }
    }
}

@Composable
fun ConversationItemView(
    item: ConversationItem,
    content: @Composable () -> Unit
) {
    val alignment = if (item.isIncoming) Alignment.Start else Alignment.End
    val color = if (item.isIncoming) MaterialTheme.colors.msgIn else MaterialTheme.colors.msgOut
    val shape = if (item.isIncoming)
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    else
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)

    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(fraction = 0.8f).align(alignment)) {
            Card(
                backgroundColor = color,
                elevation = 2.dp,
                shape = shape,
                border = BorderStroke(Dp.Hairline, MaterialTheme.colors.msgStroke),
                modifier = Modifier.align(alignment).padding(8.dp),
            ) {
                content()
            }
        }
    }
}

@Composable
fun ColumnScope.ConversationItemStatusView(item: ConversationItem, rowModifier: Modifier = Modifier) {
    val statusColor = if (item.isIncoming) MaterialTheme.colors.textPrimary else MaterialTheme.colors.privateMessageDate
    val statusAlignment = if (item.isIncoming) Alignment.End else Alignment.Start
    Row(rowModifier.align(statusAlignment)) {
        Text(
            text = getFormattedTimestamp(item.time),
            fontSize = 12.sp,
            color = statusColor,
        )
        if (!item.isIncoming) {
            val modifier = Modifier.padding(start = 4.dp).size(12.dp).align(Alignment.CenterVertically)
            val icon =
                if (item.isSeen) Icons.Filled.DoneAll // acknowledged
                else if (item.isSent) Icons.Filled.Done // sent
                else Icons.Filled.Schedule // waiting
            Icon(icon, InternationalizationUtils.i18n("access.message.sent"), modifier, statusColor)
        }
    }
}
