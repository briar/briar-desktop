package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UiPlaceholder() = Surface(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
    Text("TBD")
}
