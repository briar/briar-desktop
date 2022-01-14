package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.BriarLogo
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun StartupScreen(
    viewModel: StartupViewModel = viewModel(),
) {
    when (val holder = viewModel.currentSubViewModel.value) {
        is LoginSubViewModel -> LoginScreen(holder)
        is RegistrationSubViewModel -> RegistrationScreen(holder)
        is ErrorSubViewModel -> ErrorScreen(holder)
    }
}

@Composable
fun StartupScreenScaffold(
    title: String,
    showBackButton: Boolean = false,
    onBackButton: () -> Unit = {},
    content: @Composable () -> Unit
) = Surface {
    if (showBackButton) {
        IconButton(onClick = onBackButton) {
            Icon(Icons.Filled.ArrowBack, i18n("back"))
        }
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = CenterHorizontally
    ) {
        HeaderLine(title)
        content()
    }
}

@Composable
fun HeaderLine(title: String) =
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BriarLogo(Modifier.width(100.dp))
        Text(title, style = MaterialTheme.typography.h4)
    }
