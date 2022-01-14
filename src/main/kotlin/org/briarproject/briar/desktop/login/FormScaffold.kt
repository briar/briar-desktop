package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.briarproject.briar.desktop.ui.Constants.STARTUP_FIELDS_WIDTH
import java.util.Locale

@Composable
fun FormScaffold(
    explanationText: String?,
    buttonText: String,
    buttonClick: () -> Unit,
    buttonEnabled: Boolean,
    content: @Composable () -> Unit,
) = Column(
    modifier = Modifier.requiredWidthIn(max = STARTUP_FIELDS_WIDTH),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    if (explanationText != null) {
        Spacer(Modifier.weight(0.5f))
        Text(explanationText, style = MaterialTheme.typography.body2, modifier = Modifier.requiredWidth(STARTUP_FIELDS_WIDTH))
        Spacer(Modifier.weight(0.5f))
    } else Spacer(Modifier.weight(1.0f))
    content()
    Spacer(Modifier.weight(1.0f))
    Button(onClick = buttonClick, enabled = buttonEnabled, modifier = Modifier.fillMaxWidth()) {
        Text(buttonText.uppercase(Locale.getDefault()), color = Color.Black)
    }
}
