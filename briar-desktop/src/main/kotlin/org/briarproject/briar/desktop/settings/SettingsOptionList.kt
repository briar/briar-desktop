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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun SettingOptionsList(
    selectedSetting: SettingCategory,
    settingSelect: (SettingCategory) -> Unit
) {
    Column(Modifier.fillMaxHeight().width(COLUMN_WIDTH).background(MaterialTheme.colors.surfaceVariant)) {
        Spacer(Modifier.height(HEADER_SIZE))
        SettingOption(
            SettingCategory.GENERAL,
            Icons.Filled.Settings,
            i18n("settings.general.title"),
            selectedSetting,
            settingSelect
        )
        SettingOption(
            SettingCategory.DISPLAY,
            Icons.Filled.LightMode,
            i18n("settings.display.title"),
            selectedSetting,
            settingSelect
        )
        SettingOption(
            SettingCategory.CONNECTIONS,
            Icons.Filled.NetworkWifi,
            i18n("settings.connections.title"),
            selectedSetting,
            settingSelect
        )
        SettingOption(
            SettingCategory.SECURITY,
            Icons.Filled.Lock,
            i18n("settings.security.title"),
            selectedSetting,
            settingSelect
        )
        SettingOption(
            SettingCategory.NOTIFICATIONS,
            Icons.Filled.Notifications,
            i18n("settings.notifications.title"),
            selectedSetting,
            settingSelect
        )
        SettingOption(
            SettingCategory.ACTIONS,
            Icons.Filled.Feedback,
            i18n("settings.actions.title"),
            selectedSetting,
            settingSelect
        )
    }
}
