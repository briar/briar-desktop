package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.theme.outline

@Composable
fun MessageCounter(unread: Int, modifier: Modifier = Modifier) {
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    if (unread > 0) {
        Box(
            modifier = modifier
                .height(20.dp)
                .widthIn(min = 20.dp, max = Dp.Infinity)
                .border(2.dp, outlineColor, CircleShape)
                .background(briarSecondary, CircleShape)
                .padding(horizontal = 6.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                text = unread.toString(),
                maxLines = 1
            )
        }
    }
}
