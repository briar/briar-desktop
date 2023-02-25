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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedExposedDropDownMenu
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.settings.SettingsViewModel.NotificationProviderState
import org.briarproject.briar.desktop.theme.warningBackground
import org.briarproject.briar.desktop.theme.warningForeground
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
                DetailItem(
                    label = i18n("settings.display.theme.title"),
                    description = "${i18n("access.settings.current_value")}: " +
                        i18n("settings.display.theme.${viewModel.selectedTheme.value.name.lowercase()}") + // NON-NLS
                        ", " + i18n("access.settings.click_to_change_value")
                ) {
                    OutlinedExposedDropDownMenu(
                        values = viewModel.themesList.map {
                            i18n("settings.display.theme.${it.name.lowercase()}") // NON-NLS
                        },
                        selectedIndex = viewModel.selectedTheme.value.ordinal,
                        onChange = { viewModel.selectTheme(viewModel.themesList[it]) },
                        modifier = Modifier.widthIn(min = 200.dp)
                    )
                }

                DetailItem(
                    label = i18n("settings.display.language.title"),
                    description = "${i18n("access.settings.current_value")}: " +
                        viewModel.selectedLanguage.value.displayName +
                        ", " + i18n("access.settings.click_to_change_value")
                ) {
                    OutlinedExposedDropDownMenu(
                        values = viewModel.languageList.map { it.displayName },
                        selectedIndex = viewModel.selectedLanguage.value.ordinal,
                        onChange = { viewModel.selectLanguage(viewModel.languageList[it]) },
                        modifier = Modifier.widthIn(min = 200.dp)
                    )
                }

                DetailItem(
                    label = i18n("settings.display.ui_scale.title"),
                    description = "${i18n("access.settings.current_value")}: " +
                        viewModel.selectedUiScale.value + ", " +
                        i18n("access.settings.drag_slider_to_change_value")
                ) {
                    val uiScale = remember { mutableStateOf(viewModel.selectedUiScale.value) }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(200.dp)
                    ) {
                        Icon(Icons.Default.FormatSize, null, Modifier.scale(0.7f))
                        Slider(
                            value = uiScale.value ?: LocalDensity.current.density,
                            onValueChange = { uiScale.value = it },
                            onValueChangeFinished = { viewModel.selectUiScale(uiScale.value!!) },
                            valueRange = 1f..3f,
                            steps = 3,
                            // todo: without setting the width explicitly,
                            //  the slider takes up the whole remaining space
                            modifier = Modifier.width(150.dp)
                        )
                        Icon(Icons.Default.FormatSize, null)
                    }
                }

                DetailItem(
                    label = i18n("settings.security.title"),
                    description = i18n("access.settings.click_to_change_password")
                ) {
                    OutlinedButton(onClick = viewModel::showChangePasswordDialog) {
                        Text(i18n("settings.security.password.change"))
                    }
                }

                val notificationError = viewModel.visualNotificationProviderState is NotificationProviderState.ERROR
                val visualNotificationsChecked = !notificationError && viewModel.visualNotifications.value

                DetailItem(
                    label = i18n("settings.notifications.visual.title"),
                    description = (
                        if (visualNotificationsChecked) i18n("access.settings.currently_enabled")
                        else i18n("access.settings.currently_disabled")
                        ) + ". " + i18n("access.settings.click_to_toggle_notifications")
                ) {
                    Switch(
                        checked = visualNotificationsChecked,
                        onCheckedChange = { viewModel.toggleVisualNotifications() },
                        enabled = !notificationError
                    )
                }

                if (viewModel.visualNotificationProviderState is NotificationProviderState.ERROR) {
                    Row(
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.colors.warningBackground)
                            .padding(all = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            i18n("warning"),
                            Modifier.size(40.dp).padding(vertical = 4.dp),
                            MaterialTheme.colors.warningForeground
                        )
                        Text(
                            text = viewModel.visualNotificationProviderState.message,
                            color = MaterialTheme.colors.warningForeground,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                val soundNotificationsChecked = viewModel.soundNotifications.value

                DetailItem(
                    label = i18n("settings.notifications.sound.title"),
                    description = (
                        if (soundNotificationsChecked) i18n("access.settings.currently_enabled")
                        else i18n("access.settings.currently_disabled")
                        ) + ". " + i18n("access.settings.click_to_toggle_notifications")
                ) {
                    Switch(
                        checked = soundNotificationsChecked,
                        onCheckedChange = { viewModel.toggleSoundNotifications() },
                    )
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
fun DetailItem(
    label: String,
    description: String,
    setting: @Composable (RowScope.() -> Unit),
) = Row(
    Modifier
        .fillMaxWidth().height(HEADER_SIZE).padding(horizontal = 16.dp)
        .semantics(mergeDescendants = true) {
            // it would be nicer to derive the contentDescriptions from the descendants automatically
            // which is currently not supported in Compose for Desktop
            // see https://github.com/JetBrains/compose-jb/issues/2111
            contentDescription = description
        },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Text(label)
    setting()
}
