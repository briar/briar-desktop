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

import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.DbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

enum class SettingCategory {
    GENERAL,
    DISPLAY,
    CONNECTIONS,
    SECURITY,
    NOTIFICATIONS,
    ACTIONS
}

class SettingsViewModel
@Inject
constructor(
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val unencryptedSettings: UnencryptedSettings,
    private val encryptedSettings: EncryptedSettings,
    private val accountManager: AccountManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator,
) : DbViewModel(briarExecutors, lifecycleManager, db) {
    private val _selectedSetting = mutableStateOf(SettingCategory.DISPLAY)
    val selectedSetting = _selectedSetting.asState()

    val themesList = UnencryptedSettings.Theme.values()
    val languageList = UnencryptedSettings.Language.values()

    private val _selectedTheme = mutableStateOf(unencryptedSettings.theme)
    val selectedTheme = _selectedTheme.asState()

    private val _selectedLanguage = mutableStateOf(unencryptedSettings.language)
    val selectedLanguage = _selectedLanguage.asState()

    private val _changePasswordDialogVisible = mutableStateOf(false)
    val changePasswordDialogVisible = _changePasswordDialogVisible.asState()

    val changePasswordSubViewModel = ChangePasswordSubViewModel(accountManager, passwordStrengthEstimator)

    private val _showNotifications = mutableStateOf(encryptedSettings.showNotifications)
    val showNotifications = _showNotifications.asState()

    fun selectSetting(selectedOption: SettingCategory) {
        _selectedSetting.value = selectedOption
    }

    fun selectTheme(theme: UnencryptedSettings.Theme) {
        unencryptedSettings.theme = theme
        _selectedTheme.value = theme
    }

    fun selectLanguage(language: UnencryptedSettings.Language) {
        unencryptedSettings.language = language
        _selectedLanguage.value = language
    }

    fun showChangePasswordDialog() {
        _changePasswordDialogVisible.value = true
    }

    fun dismissChangePasswordDialog() {
        _changePasswordDialogVisible.value = false
    }

    fun toggleShowNotifications() {
        val newValue = !_showNotifications.value
        _showNotifications.value = newValue
        runOnDbThread { encryptedSettings.showNotifications = newValue }
    }
}
