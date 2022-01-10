package org.briarproject.briar.desktop.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
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
