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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ButtonType.DESTRUCTIVE
import androidx.compose.material.ButtonType.NEUTRAL
import androidx.compose.material.DialogButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.client.SessionId
import org.briarproject.briar.desktop.conversation.ConversationRequestItem.RequestType.INTRODUCTION
import org.briarproject.briar.desktop.theme.noticeIn
import org.briarproject.briar.desktop.theme.noticeOut
import org.briarproject.briar.desktop.theme.privateMessageDate
import org.briarproject.briar.desktop.theme.textPrimary
import org.briarproject.briar.desktop.theme.textSecondary
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "canBeOpened" to false,
    "answered" to false,
    "notice" to "Text of notice message.",
    "text" to "Short message",
    "time" to Instant.now().toEpochMilli(),
    "isIncoming" to true,
    "isRead" to false,
    "isSent" to false,
    "isSeen" to true,
) {
    ConversationRequestItemView(
        ConversationRequestItem(
            requestedGroupId = if (getBooleanParameter("canBeOpened")) GroupId(getRandomIdPersistent()) else null,
            requestType = INTRODUCTION,
            sessionId = SessionId(getRandomIdPersistent()),
            answered = getBooleanParameter("answered"),
            notice = getStringParameter("notice"),
            text = getStringParameter("text"),
            id = MessageId(getRandomIdPersistent()),
            groupId = GroupId(getRandomIdPersistent()),
            time = getLongParameter("time"),
            autoDeleteTimer = 0,
            isIncoming = getBooleanParameter("isIncoming"),
            isRead = getBooleanParameter("isRead"),
            isSent = getBooleanParameter("isSent"),
            isSeen = getBooleanParameter("isSeen"),
        )
    )
}

/**
 * Composable for private messages containing a request to action.
 */
@Composable
fun ConversationRequestItemView(
    m: ConversationRequestItem,
    onResponse: (Boolean) -> Unit = {},
    onOpenRequestedShareable: () -> Unit = {},
    onDelete: (MessageId) -> Unit = {},
) {
    val statusAlignment = if (m.isIncoming) Alignment.End else Alignment.Start
    val textColor = if (m.isIncoming) MaterialTheme.colors.textPrimary else Color.White
    val noticeBackground = if (m.isIncoming) MaterialTheme.colors.noticeIn else MaterialTheme.colors.noticeOut
    val noticeColor = if (m.isIncoming) MaterialTheme.colors.textSecondary else MaterialTheme.colors.privateMessageDate
    ConversationItemView(m, onDelete) {
        Column(Modifier.width(IntrinsicSize.Max)) {
            val text = m.text
            if (text != null) {
                SelectionContainer {
                    Text(
                        text,
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(12.dp, 8.dp).align(Alignment.Start)
                    )
                }
            }
            Column(
                Modifier.fillMaxWidth().background(noticeBackground).padding(12.dp, 8.dp)
            ) {
                Text(
                    text = m.notice,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = noticeColor,
                    modifier = Modifier.align(Alignment.Start),
                )

                Row(modifier = Modifier.align(statusAlignment)) {
                    if (!m.answered) {
                        DialogButton(onClick = { onResponse(false) }, text = i18n("decline"), type = DESTRUCTIVE)
                        DialogButton(onClick = { onResponse(false) }, text = i18n("accept"), type = NEUTRAL)
                    } else if (m.canBeOpened) {
                        DialogButton(onClick = onOpenRequestedShareable, text = i18n("open"), type = NEUTRAL)
                    } else {
                        Spacer(Modifier.height(8.dp))
                    }
                }
                ConversationItemStatusView(m)
            }
        }
    }
}
