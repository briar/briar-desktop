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

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.HorizontalDivider

@Composable
fun ForumsList(
    list: List<ForumsItem>,
    isSelected: (GroupId) -> Boolean,
    onGroupIdSelected: (GroupId) -> Unit,
) {
    // TODO AddForumDialog (and search bar?)
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(COLUMN_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        content = {
            LazyColumn {
                items(list) { item ->
                    GroupsCard(
                        item = item,
                        onGroupIdSelected = onGroupIdSelected,
                        selected = isSelected(item.forum.id)
                    )
                    HorizontalDivider()
                }
            }
        },
    )
}
