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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.ListItemView
import org.briarproject.briar.desktop.ui.SearchTextField
import org.briarproject.briar.desktop.ui.VerticallyScrollableArea
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    val list = remember {
        listOf(
            object : GroupItem {
                override val id: GroupId = GroupId(getRandomId())
                override val name: String =
                    "This is a test forum! This is a test forum! This is a test forum! This is a test forum!"
                override val msgCount: Int = 42
                override val unread: Int = 23
                override val timestamp: Long = (Instant.now().minusSeconds(300)).toEpochMilli()
            },
            object : GroupItem {
                override val id: GroupId = GroupId(getRandomId())
                override val name: String = "Newly added forum"
                override val msgCount: Int = 0
                override val unread: Int = 0
                override val timestamp: Long = (Instant.now()).toEpochMilli()
            },
            object : GroupItem {
                override val id: GroupId = GroupId(getRandomId())
                override val name: String = "Old forum"
                override val msgCount: Int = 123
                override val unread: Int = 5
                override val timestamp: Long = (Instant.now().minusSeconds(1200)).toEpochMilli()
            },
        )
    }

    val (selected, setSelected) = remember { mutableStateOf<GroupItem?>(null) }
    val (filterBy, setFilterBy) = remember { mutableStateOf("") }

    val filteredList = remember(filterBy) {
        list.filter {
            it.name.contains(filterBy, ignoreCase = true)
        }.sortedByDescending { it.timestamp }
    }

    GroupList(
        list = filteredList,
        isSelected = { selected?.id == it },
        filterBy = filterBy,
        onFilterSet = setFilterBy,
        onGroupItemSelected = setSelected,
        onAddButtonClicked = {},
    )
}

@Composable
fun GroupList(
    list: List<GroupItem>,
    isSelected: (GroupId) -> Boolean,
    filterBy: String,
    onFilterSet: (String) -> Unit,
    onGroupItemSelected: (GroupItem) -> Unit,
    onAddButtonClicked: () -> Unit,
) = Column(
    modifier = Modifier.fillMaxHeight().width(COLUMN_WIDTH).background(MaterialTheme.colors.surfaceVariant),
) {
    Column(
        // Align height to top bar on the right (incl. divider)
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
    ) {
        SearchTextField(
            placeholder = i18n("forum.search.title"),
            icon = Icons.Filled.AddComment,
            searchValue = filterBy,
            addButtonDescription = i18n("forum.add.title"),
            onValueChange = onFilterSet,
            onAddButtonClicked = onAddButtonClicked,
        )
    }
    VerticallyScrollableArea(modifier = Modifier.fillMaxSize()) { scrollState ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .semantics {
                    contentDescription = i18n("access.forums.list")
                }.selectableGroup()
        ) {
            items(
                items = list,
                key = { item -> item.id },
            ) { item ->
                ListItemView(
                    onSelect = { onGroupItemSelected(item) },
                    selected = isSelected(item.id),
                    // let divider start at horizontal position of text
                    dividerOffsetFromStart = (16 + 36 + 12).dp,
                ) {
                    GroupItemView(
                        groupItem = item,
                        modifier = Modifier
                            .heightIn(min = HEADER_SIZE)
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .padding(start = 16.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}
