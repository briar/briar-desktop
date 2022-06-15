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

package org.briarproject.briar.desktop.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    ChangePasswordDialog(
        viewModel.changePasswordDialogVisible.value,
        close = viewModel::dismissChangePasswordDialog,
        viewHolder = viewModel.changePasswordSubViewModel,
    )

    Row(Modifier.fillMaxSize()) {
        /* TODO: Currently commented out because there are settings in just a single category
        SettingOptionsList(
            viewModel.selectedSetting.value,
            viewModel::selectSetting
        )
        VerticalDivider()
        */
        SettingDetails(viewModel)
    }
}
