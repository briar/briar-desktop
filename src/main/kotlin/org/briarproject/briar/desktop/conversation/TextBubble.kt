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
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.theme.awayMsgBubble
import org.briarproject.briar.desktop.theme.localMsgBubble

@Composable
fun TextBubble(m: SimpleMessage) {
    if (m.local) {
        TextBubble(
            m,
            Alignment.End,
            MaterialTheme.colors.localMsgBubble,
            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp)
        )
    } else {
        TextBubble(
            m,
            Alignment.Start,
            MaterialTheme.colors.awayMsgBubble,
            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp)
        )
    }
}

@Composable
fun TextBubble(m: SimpleMessage, alignment: Alignment.Horizontal, color: Color, shape: RoundedCornerShape) {
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(fraction = 0.8f).align(alignment)) {
            Card(Modifier.align(alignment), backgroundColor = color, shape = shape) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    Text(m.message, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(m.time, Modifier.padding(end = 4.dp), fontSize = 10.sp)
                        if (m.delivered) {
                            val modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            Icon(Icons.Filled.DoneAll, "sent", modifier)
                        } else {
                            val modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            Icon(Icons.Filled.Schedule, "sending", modifier)
                        }
                    }
                }
            }
        }
    }
}
