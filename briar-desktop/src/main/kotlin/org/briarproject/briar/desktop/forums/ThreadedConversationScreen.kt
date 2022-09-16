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

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.desktop.forums.ThreadItem.Companion.UNDEFINED
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.Loader

@Composable
fun ThreadedConversationScreen(
    postsState: PostsState,
    selectedPost: MutableState<MessageId?>,
    modifier: Modifier = Modifier
) = when (postsState) {
    Loading -> Loader()
    is Loaded -> {
        val scrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        // scroll to item if needed
        if (postsState.scrollTo != null) coroutineScope.launch {
            val index = postsState.posts.indexOfFirst { it.id == postsState.scrollTo }
            if (index != -1) scrollState.scrollToItem(index, -50)
        }
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.selectableGroup()
            ) {
                items(postsState.posts) { item ->
                    ThreadItemComposable(item, selectedPost)
                    HorizontalDivider()
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(CenterEnd).fillMaxHeight()
            )
        }
    }
}

@Composable
fun ThreadItemComposable(item: ThreadItem, selectedPost: MutableState<MessageId?>) {
    val isSelected = selectedPost.value == item.id
    Text(
        text = item.text,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    MaterialTheme.colors.selectedCard
                } else {
                    MaterialTheme.colors.surfaceVariant
                }
            ).selectable(
                selected = isSelected,
                onClick = { selectedPost.value = item.id }
            )
            .padding(4.dp)
            .padding(
                start = 4.dp +
                    if (item.getLevel() == UNDEFINED) 0.dp else (item.getLevel() * 8).dp
            ),
    )
}
