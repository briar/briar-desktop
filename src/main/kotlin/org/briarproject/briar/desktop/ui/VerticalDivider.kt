package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.divider

@Composable
fun VerticalDivider() {
    Divider(color = MaterialTheme.colors.divider, modifier = Modifier.fillMaxHeight().width(1.dp))
}
