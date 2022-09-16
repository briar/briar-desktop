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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.PreviewUtils
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp

@Suppress("HardCodedStringLiteral")
fun main() = PreviewUtils.preview {
    Column(Modifier.selectableGroup()) {
        GroupsCard(
            item = object : GroupItem {
                override val id: GroupId = GroupId(getRandomId())
                override val name: String =
                    "This is a test forum! This is a test forum! This is a test forum! This is a test forum!"
                override val msgCount: Int = 42
                override val unread: Int = 23
                override val timestamp: Long = System.currentTimeMillis()
            },
            onGroupItemSelected = {},
            selected = false,
        )
    }
}

@Composable
fun GroupsCard(
    item: GroupItem,
    onGroupItemSelected: (GroupItem) -> Unit,
    selected: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = HEADER_SIZE)
            .clickable(onClick = { onGroupItemSelected(item) })
            .semantics {
                contentDescription =
                    if (selected) InternationalizationUtils.i18n("access.list.selected.yes")
                    else InternationalizationUtils.i18n("access.list.selected.no")
            },
        shape = RoundedCornerShape(0.dp),
        backgroundColor = if (selected) {
            MaterialTheme.colors.selectedCard
        } else {
            MaterialTheme.colors.surfaceVariant
        },
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            GroupCircle(item, modifier = Modifier.align(Alignment.Top).padding(vertical = 12.dp))
            Column(
                verticalArrangement = SpaceBetween,
                modifier = Modifier.align(CenterVertically).padding(start = 16.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(Start).padding(bottom = 4.dp)
                )
                Row(
                    horizontalArrangement = SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (item.msgCount > 0) {
                            i18nP("group.card.posts", item.msgCount)
                        } else {
                            i18nP("group.card.no_posts", item.msgCount)
                        },
                        style = MaterialTheme.typography.caption
                    )
                    if (item.msgCount > 0) {
                        Text(
                            text = getFormattedTimestamp(item.timestamp),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }
    }
}
