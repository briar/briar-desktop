package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.theme.awayMsgBubble
import org.briarproject.briar.desktop.theme.localMsgBubble
import org.briarproject.briar.desktop.utils.TimeUtils

@Composable
fun TextBubble(m: ConversationMessageItem) {
    val alignment = if (m.isIncoming) Alignment.Start else Alignment.End
    val color = if (m.isIncoming) MaterialTheme.colors.awayMsgBubble else MaterialTheme.colors.localMsgBubble
    val shape = if (m.isIncoming)
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp)
    else
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp)

    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(fraction = 0.8f).align(alignment)) {
            Card(Modifier.align(alignment), backgroundColor = color, shape = shape) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    Text(m.text!!, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(TimeUtils.getFormattedTimestamp(m.time), Modifier.padding(end = 4.dp), fontSize = 10.sp)
                        if (!m.isIncoming) {
                            val modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            val icon =
                                if (m.isSeen) Icons.Filled.DoneAll // acknowledged
                                else if (m.isSent) Icons.Filled.Done // sent
                                else Icons.Filled.Schedule // waiting
                            Icon(icon, "sent", modifier)
                        }
                    }
                }
            }
        }
    }
}
