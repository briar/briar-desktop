package org.briarproject.briar.desktop.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE

@Composable
fun SettingOption(
    settingCategory: SettingCategory,
    settingIcon: ImageVector,
    settingLabel: String,
    selectedSetting: SettingCategory,
    settingSelect: (SettingCategory) -> Unit
) {
    val bgColor =
        if (settingCategory == selectedSetting) MaterialTheme.colors.selectedCard else MaterialTheme.colors.surfaceVariant
    val onSel = { settingSelect(settingCategory); }
    Card(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE).clickable(onClick = onSel),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(
            modifier = Modifier.padding(start = 64.dp, end = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(settingLabel, color = MaterialTheme.colors.onSurface)
            Icon(
                settingIcon,
                settingLabel,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}
