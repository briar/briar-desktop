package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.desktop.theme.textPrimary
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant

fun main() = preview(
    "text" to "This is a long long long message that spans over several lines.\n\nIt ends here.",
    "time" to Instant.now().toEpochMilli(),
    "isIncoming" to false,
    "isRead" to false,
    "isSent" to false,
    "isSeen" to true,
) {
    ConversationMessageItemView(
        ConversationMessageItem(
            text = getStringParameter("text"),
            id = MessageId(getRandomId()),
            groupId = GroupId(getRandomId()),
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
 * Composable for normal private text messages.
 */
@Composable
fun ConversationMessageItemView(
    m: ConversationMessageItem,
    onDelete: (MessageId) -> Unit = {},
) {
    val textColor = if (m.isIncoming) MaterialTheme.colors.textPrimary else Color.White
    ConversationItemView(m, onDelete) {
        Column(
            Modifier.padding(12.dp, 8.dp)
        ) {
            Text(
                m.text!!,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            ConversationItemStatusView(m)
        }
    }
}
