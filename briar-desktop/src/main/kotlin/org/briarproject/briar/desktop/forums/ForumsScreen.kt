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

package org.briarproject.briar.desktop.forums

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.conversation.Explainer
import org.briarproject.briar.desktop.ui.ColoredIconButton
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun ForumsScreen(
    viewModel: ForumsViewModel = viewModel(),
) {
    if (viewModel.groupList.value.isEmpty()) {
        NoForumsYet {}
    } else {
        Row(modifier = Modifier.fillMaxWidth()) {
            ForumsList(
                list = viewModel.groupList,
                isSelected = viewModel::isSelected,
                filterBy = viewModel.filterBy,
                onFilterSet = viewModel::setFilterBy,
                onGroupIdSelected = viewModel::selectGroup,
                onAddButtonClicked = {},
            )
            VerticalDivider()
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                val id = viewModel.selectedGroupId.value
                if (id == null) {
                    NoForumSelected()
                } else {
                    UiPlaceholder()
                }
            }
        }
    }
}

@Composable
fun NoForumsYet(onContactAdd: () -> Unit) = Explainer(
    headline = i18n("welcome.title"),
    text = i18n("forum.empty_state.text"),
) {
    ColoredIconButton(
        icon = Icons.Filled.AddComment,
        iconSize = 20.dp,
        contentDescription = i18n("access.forums.add"),
        onClick = onContactAdd,
    )
}

@Composable
fun NoForumSelected() = Explainer(
    headline = i18n("forum.none_selected.title"),
    text = i18n("forum.none_selected.hint"),
)
