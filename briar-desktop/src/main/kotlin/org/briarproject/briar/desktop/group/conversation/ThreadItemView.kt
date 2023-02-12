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

package org.briarproject.briar.desktop.group.conversation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.forums.conversation.ForumPostItem
import org.briarproject.briar.desktop.theme.Blue500
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.LocalWindowScope
import org.briarproject.briar.desktop.ui.Tooltip
import org.briarproject.briar.desktop.ui.TrustIndicatorShort
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.getRandomForumPostHeader
import org.briarproject.briar.desktop.utils.getRandomString
import kotlin.math.min
import kotlin.random.Random

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    val list = remember {
        (0..8).map { i ->
            ForumPostItem(
                h = getRandomForumPostHeader(),
                text = getRandomString(Random.nextInt(1, 1337)),
            ).apply { setLevel(i % 7) }
        }
    }

    LazyColumn {
        items(list) { item ->
            ThreadItemView(
                item = item,
                maxNestingLevel = 3,
                selectedPost = null,
                onPostSelected = {},
            )
        }
    }
}

@Composable
fun ThreadItemView(
    item: ThreadItem,
    maxNestingLevel: Int,
    selectedPost: ThreadItem?,
    onPostSelected: (ThreadItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        NestingLevelView(
            itemLevel = item.getLevel(),
            maxNestingLevel = maxNestingLevel,
        )
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
@OptIn(ExperimentalFoundationApi::class)
fun ThreadItemContentComposable(
    item: ThreadItem,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false,
) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = spacedBy(8.dp),
                    verticalAlignment = CenterVertically,
                ) {
                    ProfileCircle(27.dp, item.author.id, item.authorInfo)
                    Text(
                        modifier = Modifier.weight(1f, fill = false),
                        text = item.authorName,
                        fontWeight = if (item.authorInfo.status == OURSELVES) Bold else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    TrustIndicatorShort(item.authorInfo.status)
                }
                Text(
                    text = getFormattedTimestamp(item.timestamp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                )
            }
            // should be changed back to verticalArrangement = spacedBy(8.dp) on the containing Column
            // when https://github.com/JetBrains/compose-jb/issues/2729 is fixed
            Spacer(Modifier.height(8.dp))
            if (!isPreview) {
                SelectionContainer {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.text,
                    )
                }
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (!isPreview) Tooltip(
            text = i18n("forum.message.new"),
            modifier = Modifier.width(8.dp),
        ) {
            AnimatedVisibility(visible = !item.isRead) {
                Box(modifier = Modifier.fillMaxSize().background(Blue500))
            }
        }
    }
}

@Composable
fun NestingLevelView(
    itemLevel: Int,
    maxNestingLevel: Int,
    modifier: Modifier = Modifier,
) {
    val level = min(itemLevel, maxNestingLevel)
    Box(modifier = modifier.width(9.dp * level)) {
        Row {
            for (i in 1..level) {
                VerticalDivider(modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (itemLevel > maxNestingLevel) Box(
            contentAlignment = Center,
            modifier = Modifier.size(24.dp)
                .background(MaterialTheme.colors.background)
                .align(Center)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colors.divider, CircleShape),
        ) {
            Text(itemLevel.toString())
        }
    }
}

@Composable
fun getMaxNestingLevel(): Int = when (LocalWindowScope.current!!.window.width.dp) {
    in (0.dp..COLUMN_WIDTH * 2) -> 5
    in (COLUMN_WIDTH * 2..COLUMN_WIDTH * 3) -> 10
    in (COLUMN_WIDTH * 2..COLUMN_WIDTH * 4) -> 25
    else -> 100
}
