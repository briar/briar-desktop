/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import org.briarproject.briar.desktop.ui.BackgroundSurface
import org.briarproject.briar.desktop.ui.Constants.STARTUP_FIELDS_WIDTH
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    var error: ErrorSubViewModel.Error by remember { mutableStateOf(RegistrationSubViewModel.RegistrationError) }

    Row(horizontalArrangement = spacedBy(8.dp)) {
        Button(onClick = { error = RegistrationSubViewModel.RegistrationError }) {
            Text("Registration")
        }
        for (e in StartResult.values().filterNot { it in listOf(SUCCESS, ALREADY_RUNNING) }) {
            Button(onClick = { error = StartupViewModel.StartingError(e) }) {
                Text(e.name.removeSuffix("_ERROR"))
            }
        }
    }

    ErrorScreen(error, {}) {}
}

@Composable
fun ErrorScreen(
    onShowAbout: () -> Unit,
    viewHolder: ErrorSubViewModel
) = ErrorScreen(viewHolder.error, onShowAbout, viewHolder.onBackButton)

@Composable
fun ErrorScreen(
    error: ErrorSubViewModel.Error,
    onShowAbout: () -> Unit,
    onBackButton: (() -> Unit)?,
) {
    val text = when (error) {
        is RegistrationSubViewModel.RegistrationError -> i18n("startup.failed.registration")
        is StartupViewModel.StartingError -> {
            when (error.error) {
                CLOCK_ERROR -> i18n("startup.failed.clock_error")
                DB_ERROR -> i18n("startup.failed.db_error")
                DATA_TOO_OLD_ERROR -> i18n("startup.failed.data_too_old_error")
                DATA_TOO_NEW_ERROR -> i18n("startup.failed.data_too_new_error")
                SERVICE_ERROR -> i18n("startup.failed.service_error")
                else -> ""
            }
        }
    }

    ErrorScreen(text, onShowAbout, onBackButton)
}

@Composable
fun ErrorScreen(
    text: String,
    onShowAbout: () -> Unit,
    onBackButton: (() -> Unit)? = null,
) = BackgroundSurface {
    Box {
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
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.widthIn(max = STARTUP_FIELDS_WIDTH)
            )
        }

        if (onBackButton != null) {
            IconButton(onClick = onBackButton) {
                Icon(Icons.Filled.ArrowBack, i18n("back"))
            }
        }

        IconButton(
            onClick = onShowAbout,
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Icon(Icons.Filled.Info, i18n("access.about_briar_desktop"))
        }
    }
}
