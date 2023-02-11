/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.conversation.Explainer
import org.briarproject.briar.desktop.ui.ColoredIconButton
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun <T : GroupItem> GroupScreen(
    strings: GroupStrings,
    viewModel: GroupListViewModel<T>,
    addGroupDialog: @Composable (MutableState<Boolean>) -> Unit,
    conversationScreen: @Composable () -> Unit,
) {
    val addDialogVisible = remember { mutableStateOf(false) }
    addGroupDialog(addDialogVisible)

    if (viewModel.noGroupsYet.value) {
        NoGroupsYet(strings) { addDialogVisible.value = true }
    } else {
        Row(modifier = Modifier.fillMaxWidth()) {
            GroupList(
                strings = strings,
                list = viewModel.list.value,
                isSelected = viewModel::isSelected,
                filterBy = viewModel.filterBy.value,
                onFilterSet = viewModel::setFilterBy,
                onGroupItemSelected = viewModel::selectGroup,
                onAddButtonClicked = { addDialogVisible.value = true },
            )
            VerticalDivider()
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                if (viewModel.selectedGroupId.value == null) {
                    NoGroupSelected(strings)
                } else {
                    conversationScreen()
                }
            }
        }
    }
}

@Composable
fun NoGroupsYet(strings: GroupStrings, onAdd: () -> Unit) = Explainer(
    headline = i18n("welcome.title"),
    text = strings.noGroupsYet,
) {
    ColoredIconButton(
        icon = Icons.Filled.AddComment,
        iconSize = 20.dp,
        contentDescription = strings.addButtonDescription,
        onClick = onAdd,
    )
}

@Composable
fun NoGroupSelected(strings: GroupStrings) = Explainer(
    headline = strings.noGroupSelectedTitle,
    text = strings.noGroupSelectedText,
)
