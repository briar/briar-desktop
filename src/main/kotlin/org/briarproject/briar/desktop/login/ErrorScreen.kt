package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.ALREADY_RUNNING
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.CLOCK_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.DATA_TOO_NEW_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.DATA_TOO_OLD_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.DB_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.SERVICE_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.SUCCESS
import org.briarproject.briar.desktop.theme.Red500
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

fun main() = preview {
    var error by remember { mutableStateOf(SUCCESS) }

    Row(horizontalArrangement = spacedBy(8.dp)) {
        for (e in StartResult.values().filterNot { it in listOf(SUCCESS, ALREADY_RUNNING) }) {
            Button(onClick = { error = e }) {
                Text(e.name.removeSuffix("_ERROR"))
            }
        }
    }

    ErrorScreen(error) {}
}

@Composable
fun ErrorScreen(viewHolder: ErrorViewHolder) =
    ErrorScreen(viewHolder.error, viewHolder.onBackButton)

@Composable
fun ErrorScreen(
    error: StartResult,
    onBackButton: () -> Unit,
) = Surface {
    IconButton(onClick = onBackButton) {
        Icon(Icons.Filled.ArrowBack, i18n("back"))
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = spacedBy(32.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = i18n("error"),
            modifier = Modifier.size(128.dp),
            tint = Red500
        )

        Text(i18n("sorry"), style = MaterialTheme.typography.h5)

        val text = when (error) {
            CLOCK_ERROR -> i18n("startup.failed.clock_error")
            DB_ERROR -> i18n("startup.failed.db_error")
            DATA_TOO_OLD_ERROR -> i18n("startup.failed.data_too_old_error")
            DATA_TOO_NEW_ERROR -> i18n("startup.failed.data_too_new_error")
            SERVICE_ERROR -> i18n("startup.failed.service_error")
            else -> ""
        }
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.widthIn(max = 400.dp)
        )
    }
}
