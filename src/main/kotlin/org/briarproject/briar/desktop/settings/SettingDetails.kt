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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun SettingDetails(viewModel: SettingsViewModel) {
    Surface(Modifier.fillMaxSize()) {
        when (viewModel.selectedSetting.value) {
            SettingCategory.GENERAL -> {
                SettingDetail(i18n("settings.general.title")) {}
            }
            SettingCategory.DISPLAY -> {
                // TODO: Change this to `settings.display.title` once more categories are implemented
                SettingDetail(i18n("settings.title")) {
                    DetailItem {
                        Text(i18n("settings.display.theme"))
                        val isDarkMode = viewModel.isDarkMode.value
                        Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleTheme() })
                    }
                }
            }
            SettingCategory.CONNECTIONS -> {
                SettingDetail(i18n("settings.connections.title")) {}
            }
            SettingCategory.SECURITY -> {
                SettingDetail(i18n("settings.security.title")) {}
            }
            SettingCategory.NOTIFICATIONS -> {
                SettingDetail(i18n("settings.notifications.title")) {}
            }
            SettingCategory.ACTIONS -> {
                SettingDetail(i18n("settings.actions.title")) {}
            }
        }
    }
}

@Composable
fun SettingDetail(header: String, content: @Composable (ColumnScope.() -> Unit)) =
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().height(HEADER_SIZE).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(header, style = MaterialTheme.typography.h4, color = MaterialTheme.colors.onSurface)
        }
        content()
    }

@Composable
fun DetailItem(content: @Composable (RowScope.() -> Unit)) = Row(
    Modifier.fillMaxWidth().height(HEADER_SIZE).padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    content()
}
