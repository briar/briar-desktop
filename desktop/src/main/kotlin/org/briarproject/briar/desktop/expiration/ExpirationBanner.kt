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

package org.briarproject.briar.desktop.expiration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.expiration.ExpirationUtils.periodicallyCheckIfExpired
import org.briarproject.briar.desktop.theme.warningBackground
import org.briarproject.briar.desktop.theme.warningForeground
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    Column {
        ExpirationBanner {}
    }
}

@Composable
// [ColumnScope] needed for proper [AnimatedVisibility] implementation
fun ColumnScope.ExpirationBanner(onExpired: () -> Unit) {

    var daysLeft by remember { mutableStateOf(0) }
    var expired by remember { mutableStateOf(false) }

    var hideTimestamp by remember { mutableStateOf(0L) }

    var showExpirationBanner by remember { mutableStateOf(value = true) }

    LaunchedEffect(Unit) {
        periodicallyCheckIfExpired(
            reportDaysLeft = { daysLeft = it },
            reportHideThreshold = { showExpirationBanner = hideTimestamp <= it },
            onExpiry = { showExpirationBanner = false; expired = true; onExpired() },
        )
    }

    AnimatedVisibility(showExpirationBanner && !expired) {
        ExpirationBanner(daysLeft) {
            hideTimestamp = System.currentTimeMillis()
            showExpirationBanner = false
        }
    }
}

@Composable
fun ExpirationBanner(
    daysLeft: Int,
    hide: () -> Unit,
) = Surface(
    color = MaterialTheme.colors.warningBackground,
    contentColor = MaterialTheme.colors.warningForeground,
    modifier = Modifier.fillMaxWidth()
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(Icons.Filled.Warning, i18n("warning"), Modifier.size(40.dp).padding(vertical = 4.dp))
        val text = if (daysLeft == 0)
            "${i18n("expiration.banner.part1.zero")} ${i18n("expiration.banner.part2")}"
        else
            "${i18nP("expiration.banner.part1.nozero", daysLeft)} ${i18n("expiration.banner.part2")}"
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f, true).padding(vertical = 12.dp)
        )
        IconButton(hide, modifier = Modifier.padding(vertical = 4.dp)) {
            Icon(Icons.Filled.Close, i18n("hide"), Modifier.size(24.dp))
        }
    }
}
