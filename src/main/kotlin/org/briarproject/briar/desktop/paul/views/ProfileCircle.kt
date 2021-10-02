package org.briarproject.briar.desktop.paul.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProfileCircle(size: Dp, input: ByteArray) {
    Canvas(Modifier.size(size).clip(CircleShape).border(2.dp, Color.White, CircleShape)) {
        Identicon(input, this.size.width, this.size.height).draw(this)
    }
}
