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

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.blog.sharing.BlogSharingStatusDrawerContent
import org.briarproject.briar.desktop.blog.sharing.BlogSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.SharingStrings
import org.briarproject.briar.desktop.threadedgroup.sharing.ThreadedGroupSharingActionDrawerContent
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.getInfoDrawerHandler
import org.briarproject.briar.desktop.utils.DesktopUtils.browseLinkIfSupported
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun BlogScreen(viewModel: FeedViewModel = viewModel()) {
    LinkClickedDialog(
        link = viewModel.clickedLink.value,
        visible = viewModel.openLinkWarningDialogVisible.value,
        onDismissed = { viewModel.dismissOpenLinkWarningDialog() },
        onConfirmed = {
            viewModel.dismissOpenLinkWarningDialog()
            browseLinkIfSupported(viewModel.clickedLink.value)
        },
    )
    Scaffold(
        topBar = {
            // only show header if some blog is selected
            if (viewModel.selectedBlog.value != null) {
                BlogHeader(
                    blogSharingViewModel = viewModel.blogSharingViewModel,
                    onBackClick = { viewModel.selectBlog(null) }
                )
            }
        },
        content = { padding ->
            if (viewModel.isLoading.value) {
                Box(
                    contentAlignment = Center,
                    modifier = Modifier.padding(padding).fillMaxSize()
                ) {
                    CircularProgressIndicator(Modifier.padding(16.dp))
                }
            } else {
                if (viewModel.posts.value.isEmpty()) {
                    Box(
                        contentAlignment = Center,
                        modifier = Modifier.padding(padding).fillMaxSize()
                    ) {
                        Text(i18n("blog.empty.state"), Modifier.padding(16.dp))
                    }
                } else {
                    FeedScreen(
                        posts = viewModel.posts.value,
                        unreadFabsInfo = viewModel,
                        onItemSelected = viewModel::selectPost,
                        onBlogSelected = if (viewModel.selectedBlog.value == null) viewModel::selectBlog else null,
                        onBlogPostsVisible = viewModel::onPostsVisible,
                        onLinkClicked = viewModel::showOpenLinkWarningDialog,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        },
        bottomBar = {
            // only show input field if no blog is selected, or if a blog and a post are selected
            if (viewModel.selectedBlog.value == null || viewModel.selectedPost.value != null) {
                val onCloseReply = { viewModel.selectPost(null) }
                BlogInput(viewModel.selectedPost.value, onCloseReply) { text ->
                    viewModel.createBlogPost(text)
                }
            }
        }
    )
}

@Composable
private fun BlogHeader(
    blogSharingViewModel: BlogSharingViewModel,
    onBackClick: () -> Unit,
) {
    val isMenuOpen = remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
        Row(
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth().align(Center).padding(horizontal = 16.dp),
        ) {
            Row(
                horizontalArrangement = spacedBy(8.dp),
                verticalAlignment = CenterVertically,
            ) {
                IconButton(
                    icon = Icons.Filled.ArrowBack,
                    contentDescription = i18n("blog.back"),
                    onClick = onBackClick,
                )
                Column {
                    Text(
                        text = blogSharingViewModel.contactName.value,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h2,
                    )
                    val sharingInfo = blogSharingViewModel.sharingInfo.value
                    Text(
                        text = i18nF("blog.sharing.status.with", sharingInfo.total, sharingInfo.online)
                    )
                }
            }
            Row(
                horizontalArrangement = spacedBy(4.dp),
                verticalAlignment = CenterVertically,
            ) {
                val infoDrawerHandler = getInfoDrawerHandler()
                IconButton(
                    icon = Icons.Filled.Share,
                    contentDescription = i18n("blog.sharing.action.title"),
                    onClick = {
                        infoDrawerHandler.open {
                            ThreadedGroupSharingActionDrawerContent(
                                close = infoDrawerHandler::close,
                                viewModel = blogSharingViewModel,
                                strings = object : SharingStrings {
                                    override val sharingActionTitle = i18n("blog.sharing.action.title")
                                    override val sharingActionClose = i18n("access.forum.sharing.action.close")
                                    override val sharingActionNoContacts = i18n("blog.sharing.action.no_contacts")
                                },
                            )
                        }
                    },
                )
                IconButton(
                    icon = Icons.Filled.MoreVert,
                    contentDescription = i18n("access.menu"),
                    onClick = { isMenuOpen.value = true },
                ) {
                    BlogDropdownMenu(
                        expanded = isMenuOpen.value,
                        onClose = { isMenuOpen.value = false },
                        blogSharingViewModel = blogSharingViewModel,
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.align(BottomCenter))
    }
}

@Composable
private fun BlogDropdownMenu(
    expanded: Boolean,
    onClose: () -> Unit,
    blogSharingViewModel: BlogSharingViewModel,
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
) {
    val infoDrawerHandler = getInfoDrawerHandler()
    DropdownMenuItem(
        onClick = {
            onClose()
            infoDrawerHandler.open {
                BlogSharingStatusDrawerContent(
                    close = infoDrawerHandler::close,
                    viewModel = blogSharingViewModel,
                )
            }
        }
    ) {
        Text(
            i18n("blog.sharing.status.title"),
            style = MaterialTheme.typography.body2,
        )
    }
}
