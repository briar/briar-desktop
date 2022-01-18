package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun BriarDialog(
    onClose: () -> Unit,
    content: @Composable BoxWithConstraintsScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        // This adds a scrim that dims the background to make the dialog stand out visually
        Box(
            modifier = Modifier.requiredSize(maxWidth, maxHeight)
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.32f))
                .clickable(
                    // prevent visual indication
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClose()
                }
        )

        Surface(modifier = Modifier.align(Alignment.Center), shape = RoundedCornerShape(8.dp)) {
            content()
        }
    }
}
