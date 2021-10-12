package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.divider

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.divider, thickness = 1.dp, modifier = modifier.fillMaxWidth())
}
