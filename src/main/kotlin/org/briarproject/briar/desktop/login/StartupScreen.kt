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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.BackgroundSurface
import org.briarproject.briar.desktop.ui.BriarLogo
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun StartupScreen(
    onShowAbout: () -> Unit,
    viewModel: StartupViewModel = viewModel(),
) {
    when (val holder = viewModel.currentSubViewModel.value) {
        is LoginSubViewModel -> LoginScreen(onShowAbout, holder)
        is RegistrationSubViewModel -> RegistrationScreen(onShowAbout, holder)
        is ErrorSubViewModel -> ErrorScreen(onShowAbout, holder)
    }
}

@Composable
fun StartupScreenScaffold(
    title: String,
    showBackButton: Boolean = false,
    onBackButton: () -> Unit = {},
    onShowAbout: () -> Unit = {},
    content: @Composable () -> Unit
) = BackgroundSurface {
    Box {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = CenterHorizontally
        ) {
            HeaderLine(title)
            content()
        }

        if (showBackButton) {
            IconButton(
                onClick = onBackButton,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
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

@Composable
fun HeaderLine(title: String) =
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BriarLogo(Modifier.width(100.dp))
        Text(title, style = MaterialTheme.typography.h4)
    }
