/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.client.SessionId
import org.briarproject.briar.desktop.conversation.ConversationRequestItem.RequestType.INTRODUCTION
import org.briarproject.briar.desktop.theme.msgIn
import org.briarproject.briar.desktop.theme.msgOut
import org.briarproject.briar.desktop.theme.msgStroke
import org.briarproject.briar.desktop.theme.privateMessageDate
import org.briarproject.briar.desktop.theme.textPrimary
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    LazyColumn {
        item {
            ConversationNoticeItemView(
                ConversationNoticeItem(
                    notice = "Text of notice message.",
                    text = "Let's test a received notice message.",
                    id = MessageId(getRandomIdPersistent()),
                    groupId = GroupId(getRandomIdPersistent()),
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
                    id = MessageId(getRandomIdPersistent()),
                    groupId = GroupId(getRandomIdPersistent()),
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
                    sessionId = SessionId(getRandomIdPersistent()),
                    answered = false,
                    notice = "Text of notice message.",
                    text = "Short message.",
                    id = MessageId(getRandomIdPersistent()),
                    groupId = GroupId(getRandomIdPersistent()),
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
                    notice = "Text of notice message.",
                    text = "This is a long long long message that spans over several lines.\n\nIt ends here.",
                    id = MessageId(getRandomIdPersistent()),
                    groupId = GroupId(getRandomIdPersistent()),
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
                    id = MessageId(getRandomIdPersistent()),
                    groupId = GroupId(getRandomIdPersistent()),
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
                    notice = "Text of notice message.",
                    text = null,
                    id = MessageId(getRandomIdPersistent()),
                    groupId = GroupId(getRandomIdPersistent()),
                    time = Instant.now().toEpochMilli(),
                    autoDeleteTimer = 0,
                    isIncoming = false,
                    isRead = false,
                    isSent = true,
                    isSeen = true,
                )
            )
        }
    }
}

/**
 * Base Composable for all kind of messages in private chats.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationItemView(
    item: ConversationItem,
    onDelete: (MessageId) -> Unit = {},
    content: @Composable () -> Unit
) {
    val arrangement = if (item.isIncoming) Arrangement.Start else Arrangement.End
    val alignment = if (item.isIncoming) Alignment.CenterStart else Alignment.CenterEnd
    val color = if (item.isIncoming) MaterialTheme.colors.msgIn else MaterialTheme.colors.msgOut
    val shape = if (item.isIncoming)
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    else
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)

    Row(Modifier.fillMaxWidth(), arrangement) {
        Box(Modifier.fillMaxWidth(0.8f), contentAlignment = alignment) {
            ContextMenuArea(
                items = {
                    listOf(
                        ContextMenuItem(i18n("conversation.delete.single")) { onDelete(item.id) }
                    )
                }
            ) {
                Card(
                    backgroundColor = color,
                    elevation = 2.dp,
                    shape = shape,
                    border = BorderStroke(Dp.Hairline, MaterialTheme.colors.msgStroke),
                    modifier = Modifier.padding(8.dp),
                ) {
                    content()
                }
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
            style = MaterialTheme.typography.caption,
            color = statusColor,
        )
        if (!item.isIncoming) {
            val modifier = Modifier.padding(start = 4.dp).size(12.dp).align(Alignment.CenterVertically)
            val icon =
                if (item.isSeen) Icons.Filled.DoneAll // acknowledged
                else if (item.isSent) Icons.Filled.Done // sent
                else Icons.Filled.Schedule // waiting
            Icon(icon, i18n("access.message.sent"), modifier, statusColor)
        }
    }
}
