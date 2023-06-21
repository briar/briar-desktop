/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

package org.briarproject.briar.desktop.blog

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.desktop.conversation.reallyVisibleItemsInfo
import org.briarproject.briar.desktop.ui.Constants
import org.briarproject.briar.desktop.ui.isWindowFocused
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "text" to "This is a test blog post!\n\nThis is a blog post! This is a blog post! This is a blog post!",
    "timestamp" to Instant.now().toEpochMilli(),
    "numPosts" to 42,
) {
    val items = buildList {
        for (i in 1..getIntParameter("numPosts")) {
            add(getRandomBlogPost(getStringParameter("text"), getLongParameter("timestamp")))
        }
    }
    FeedScreen(items, {})
}

@Composable
fun FeedScreen(posts: List<BlogPost>, onItemSelected: (BlogPost) -> Unit, modifier: Modifier = Modifier) {
    val scrollState = rememberLazyListState()
    // scroll to first unread item if needed
    val lastUnreadIndex = posts.indexOfLast { item -> !item.isRead }
    if (lastUnreadIndex > -1) LaunchedEffect(posts) {
        scrollState.scrollToItem(lastUnreadIndex, -50)
    }
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.padding(end = 8.dp).selectableGroup()
        ) {
            items(
                items = posts,
                key = { item -> item.id },
            ) { item ->
                BlogPostView(
                    item = item,
                    onItemRepeat = onItemSelected,
                    modifier = Modifier
                        .heightIn(min = Constants.HEADER_SIZE)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(start = 16.dp, end = 8.dp)
                )
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
        if (isWindowFocused()) {
            // if Briar Desktop currently has focus,
            // mark all posts visible on the screen as read after some delay
            LaunchedEffect(
                posts,
                scrollState.firstVisibleItemIndex,
                scrollState.firstVisibleItemScrollOffset
            ) {
                delay(2_500)
                val visibleMessageIds = scrollState.layoutInfo.reallyVisibleItemsInfo.map {
                    it.key as MessageId
                }
                // TODO onBlogPostsVisible(visibleMessageIds)
            }
        }
    }
}
