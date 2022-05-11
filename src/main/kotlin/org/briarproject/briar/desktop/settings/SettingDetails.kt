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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedExposedDropDownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun SettingDetails(viewModel: SettingsViewModel) {
    when (viewModel.selectedSetting.value) {
        SettingCategory.GENERAL -> {
            SettingDetail(i18n("settings.general.title")) {}
        }
        SettingCategory.DISPLAY -> {
            // TODO: Change this to `settings.display.title` once more categories are implemented
            SettingDetail(i18n("settings.title")) {
                DetailItem {
                    Text(i18n("settings.display.theme.title"))

                    OutlinedExposedDropDownMenu(
                        values = viewModel.themesList.map { i18n("settings.display.theme.${it.name.lowercase()}") },
                        selectedIndex = viewModel.selectedTheme.value.ordinal,
                        onChange = { viewModel.selectTheme(viewModel.themesList[it]) },
                        modifier = Modifier.widthIn(min = 200.dp)
                    )
                }

                DetailItem {
                    Text(i18n("settings.display.language.title"))

                    OutlinedExposedDropDownMenu(
                        values = viewModel.languageList.map {
                            if (it == UnencryptedSettings.Language.DEFAULT) i18n("settings.display.language.auto")
                            else it.locale.getDisplayLanguage(it.locale)
                        },
                        selectedIndex = viewModel.selectedLanguage.value.ordinal,
                        onChange = { viewModel.selectLanguage(viewModel.languageList[it]) },
                        modifier = Modifier.widthIn(min = 200.dp)
                    )
                }

                DetailItem {
                    Text(i18n("settings.security.title"))

                    OutlinedButton(onClick = viewModel::showChangePasswordDialog) {
                        Text(i18n("settings.security.password.change"))
                    }
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
