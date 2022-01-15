package org.briarproject.briar.desktop.expiration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.briarproject.briar.desktop.expiration.ExpirationUtils.getDaysLeft
import org.briarproject.briar.desktop.expiration.ExpirationUtils.isExpired
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import kotlin.time.Duration.Companion.hours

fun main() = preview {
    Column {
        ExpirationBanner {}
    }
}

@Composable
fun ExpirationBanner(onExpired: () -> Unit) {

    var daysLeft by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        launch {
            while (true) {
                daysLeft = getDaysLeft()
                if (isExpired()) {
                    onExpired()
                    break
                }
                delay(1.hours.inWholeMilliseconds)
            }
        }
    }

    ExpirationBanner(daysLeft)
}

@Composable
fun ExpirationBanner(
    daysLeft: Int,
) = Surface(
    color = MaterialTheme.colors.error,
    modifier = Modifier.fillMaxWidth()
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(Icons.Filled.Warning, i18n("warning"))
        Text(i18nP("expiration.banner", daysLeft))
    }
}
