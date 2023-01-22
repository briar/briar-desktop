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

package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ListItemView(
    selected: Boolean? = null,
    onSelect: () -> Unit = {},
    dividerOffsetFromStart: Dp = 0.dp,
    multiSelectWithCheckbox: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Column(modifier.fillMaxWidth()) {
    val bgColor = if (selected != null && selected) MaterialTheme.colors.selectedCard else Color.Transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .then(
                if (selected != null) {
                    Modifier
                        .semantics {
                            contentDescription = if (selected) i18n("access.list.selected.yes")
                            else i18n("access.list.selected.no")
                            // todo: stateDescription apparently not used
                            // stateDescription = if (selected) "selected" else "not selected"
                        }
                        .selectable(selected, onClick = onSelect, role = Role.Button)
                } else Modifier
            )
    ) {
        if (multiSelectWithCheckbox) {
            Checkbox(
                checked = selected ?: false,
                onCheckedChange = { onSelect() },
                enabled = selected != null,
            )
        }
        content()
    }
    HorizontalDivider(Modifier.padding(start = dividerOffsetFromStart))
}
