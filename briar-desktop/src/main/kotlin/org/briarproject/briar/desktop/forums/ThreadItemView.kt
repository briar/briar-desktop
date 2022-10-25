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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.Blue500
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.TrustIndicator
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.getRandomForumPostHeader
import org.briarproject.briar.desktop.utils.getRandomString
import kotlin.random.Random

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    LazyColumn {
        for (i in 1..5) {
            item {
                ThreadItemView(
                    item = ForumPostItem(
                        h = getRandomForumPostHeader(),
                        text = getRandomString(Random.nextInt(1, 1337)),
                    ).apply { setLevel(Random.nextInt(0, 6)) },
                    selectedPost = null,
                ) {}
            }
        }
    }
}

@Composable
fun ThreadItemView(
    item: ThreadItem,
    selectedPost: ThreadItem?,
    onPostSelected: (ThreadItem) -> Unit,
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        for (i in 1..item.getLevel()) {
            VerticalDivider(modifier = Modifier.padding(start = 8.dp))
        }
        val isSelected = selectedPost == item
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isSelected) {
                        Modifier.border(3.dp, Blue500)
                    } else Modifier
                ).selectable(
                    selected = isSelected,
                    onClick = { onPostSelected(item) }
                ),
        ) {
            HorizontalDivider()
            ThreadItemContentComposable(item)
        }
    }
}

@Composable
fun ThreadItemContentComposable(
    item: ThreadItem,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false,
) {
    Column(
        modifier = modifier.then(Modifier.padding(8.dp)),
        verticalArrangement = spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = spacedBy(8.dp),
                verticalAlignment = CenterVertically,
            ) {
                // TODO load and cache profile images, if available
                ProfileCircle(20.dp, item.author.id.bytes)
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = item.authorName,
                    fontWeight = if (item.authorInfo.status == OURSELVES) Bold else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TrustIndicator(item.authorInfo.status)
            }
            Text(
                text = getFormattedTimestamp(item.timestamp),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = item.text,
            maxLines = if (isPreview) 1 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
