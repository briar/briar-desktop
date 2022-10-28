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
import kotlinx.coroutines.delay
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.desktop.conversation.reallyVisibleItemsInfo
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.ui.isWindowFocused

@Composable
fun ThreadedConversationScreen(
    postsState: PostsState,
    selectedPost: ThreadItem?,
    onPostSelected: (ThreadItem) -> Unit,
    onPostsVisible: (List<MessageId>) -> Unit,
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
                items(postsState.posts, key = { item -> item.id }) { item ->
                    ThreadItemView(item, selectedPost, onPostSelected)
                }
            }
            UnreadFabs(scrollState, postsState)
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(CenterEnd).fillMaxHeight()
            )
            if (isWindowFocused()) {
                // if Briar Desktop currently has focus,
                // mark all posts visible on the screen for more than 1s milliseconds as read
                LaunchedEffect(
                    postsState,
                    scrollState.firstVisibleItemIndex,
                    scrollState.firstVisibleItemScrollOffset
                ) {
                    delay(2_500)
                    val visibleMessageIds = scrollState.layoutInfo.reallyVisibleItemsInfo.map {
                        it.key as MessageId
                    }
                    onPostsVisible(visibleMessageIds)
                }
            }
        }
    }
}
