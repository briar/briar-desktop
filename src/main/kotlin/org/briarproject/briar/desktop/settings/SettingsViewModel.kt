package org.briarproject.briar.desktop.settings

import androidx.compose.runtime.mutableStateOf
import org.briarproject.briar.desktop.viewmodel.ViewModel
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
constructor() : ViewModel {
    private val _selectedSetting = mutableStateOf(SettingCategory.DISPLAY)
    val selectedSetting = _selectedSetting.asState()

    private val _isDarkMode = mutableStateOf(true)
    val isDarkMode = _isDarkMode.asState()

    fun selectSetting(selectedOption: SettingCategory) {
        _selectedSetting.value = selectedOption
    }

    fun toggleTheme() {
        _isDarkMode.value = !isDarkMode.value
    }
}
