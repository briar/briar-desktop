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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.briarproject.briar.desktop.conversation.firstReallyVisibleItemIndex
import org.briarproject.briar.desktop.conversation.lastReallyVisibleItemIndex
import org.briarproject.briar.desktop.theme.ChevronDown
import org.briarproject.briar.desktop.theme.ChevronUp
import org.briarproject.briar.desktop.ui.NumberBadge
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun BoxScope.UnreadFabs(scrollState: LazyListState, postsState: Loaded) {
    val coroutineScope = rememberCoroutineScope()

    // remember first really visible item index based on scroll offset
    val firstReallyVisibleItemIndex = remember(scrollState.firstVisibleItemScrollOffset) {
        scrollState.firstReallyVisibleItemIndex
    }
    // remember unread info to avoid unnecessary re-computations
    val unreadInfo = remember(postsState, firstReallyVisibleItemIndex) {
        postsState.unreadBeforeIndex(firstReallyVisibleItemIndex)
    }
    AnimatedVisibility(
        visible = unreadInfo.numUnread > 0,
        modifier = Modifier.align(TopEnd).padding(16.dp),
    ) {
        UnreadPostsFab(
            imageVector = Icons.Default.ChevronUp,
            numUnread = unreadInfo.numUnread,
            onClick = {
                coroutineScope.launch {
                    if (unreadInfo.nextUnreadIndex != null) {
                        scrollState.animateScrollToItem(unreadInfo.nextUnreadIndex)
                    }
                }
            },
        )
    }

    // remember last really visible item index based on scroll offset and item count
    val lastReallyVisibleItemIndex = remember(
        scrollState.layoutInfo.totalItemsCount, // this is 0 initially as the offset
        scrollState.firstVisibleItemScrollOffset,
    ) {
        scrollState.lastReallyVisibleItemIndex
    }
    // remember unread info to avoid unnecessary re-computations
    val bottomUnreadInfo = remember(postsState, lastReallyVisibleItemIndex) {
        postsState.unreadAfterIndex(lastReallyVisibleItemIndex)
    }
    AnimatedVisibility(
        visible = bottomUnreadInfo.numUnread > 0,
        modifier = Modifier.align(BottomEnd).padding(16.dp),
    ) {
        UnreadPostsFab(
            imageVector = Icons.Default.ChevronDown,
            numUnread = bottomUnreadInfo.numUnread,
            onClick = {
                coroutineScope.launch {
                    if (bottomUnreadInfo.nextUnreadIndex != null) scrollState.animateScrollToItem(
                        index = bottomUnreadInfo.nextUnreadIndex,
                        // scroll only down half the screen
                        scrollOffset = -scrollState.layoutInfo.viewportEndOffset / 2,
                    )
                }
            },
        )
    }
}

@Composable
fun UnreadPostsFab(
    imageVector: ImageVector,
    numUnread: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    FloatingActionButton(onClick) {
        Icon(imageVector, i18n("access.message.jump_to_unread"))
    }
    NumberBadge(
        num = numUnread,
        modifier = Modifier.align(TopEnd).offset(3.dp, (-3).dp)
    )
}
