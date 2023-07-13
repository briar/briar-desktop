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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun BlogScreen(onSideBarClickedKey: Any, viewModel: FeedViewModel = viewModel()) {
    LaunchedEffect(onSideBarClickedKey) { viewModel.selectBlog(null) }
    Scaffold(
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
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        },
        bottomBar = {
            val onCloseReply = { viewModel.selectPost(null) }
            BlogInput(viewModel.selectedPost.value, onCloseReply) { text ->
                viewModel.createBlogPost(text)
            }
        }
    )
}
