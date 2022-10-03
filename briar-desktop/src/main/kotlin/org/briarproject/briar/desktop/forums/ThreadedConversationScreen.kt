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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.ui.Loader

@Composable
fun ThreadedConversationScreen(
    postsState: PostsState,
    selectedPost: ThreadItem?,
    onPostSelected: (ThreadItem) -> Unit,
    modifier: Modifier = Modifier,
) = when (postsState) {
    Loading -> Loader()
    is Loaded -> {
        val scrollState = rememberLazyListState()
        // scroll to item if needed
        if (postsState.scrollTo != null) LaunchedEffect(postsState) {
            val index = postsState.posts.indexOfFirst { it.id == postsState.scrollTo }
            if (index != -1) scrollState.scrollToItem(index, -50)
        }
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.selectableGroup()
            ) {
                items(postsState.posts) { item ->
                    ThreadItemComposable(item, selectedPost, onPostSelected)
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(CenterEnd).fillMaxHeight()
            )
        }
    }
}
