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

package org.briarproject.briar.desktop.blog

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.sync.event.GroupRemovedEvent
import org.briarproject.briar.api.blog.BlogCommentHeader
import org.briarproject.briar.api.blog.BlogManager
import org.briarproject.briar.api.blog.BlogPostFactory
import org.briarproject.briar.api.blog.BlogPostHeader
import org.briarproject.briar.api.blog.event.BlogPostAddedEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asList
import org.briarproject.briar.desktop.viewmodel.asState
import org.briarproject.briar.util.HtmlUtils
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val blogManager: BlogManager,
    private val blogPostFactory: BlogPostFactory,
    private val identityManager: IdentityManager,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading.asState()

    private val _posts = mutableStateListOf<BlogPost>()
    val posts = _posts.asList()

    private val _selectedPost = mutableStateOf<BlogPost?>(null)
    val selectedPost = _selectedPost.asState()

    init {
        runOnDbThreadWithTransaction(true, this::loadAllBlogPosts)
    }

    @Suppress("HardCodedStringLiteral")
    override fun eventOccurred(e: Event) {
        if (e is BlogPostAddedEvent) {
            LOG.info("Blog post added")
            onBlogPostAdded(e.header, e.isLocal)
        } else if (e is GroupRemovedEvent && e.group.clientId == BlogManager.CLIENT_ID) {
            LOG.info("Blog removed")
            onBlogRemoved(e.group.id)
        }
    }

    @DatabaseExecutor
    private fun loadAllBlogPosts(txn: Transaction) {
        val posts = blogManager.getBlogIds(txn).flatMap { g ->
            loadBlogPosts(txn, g)
        }.sortedByDescending { blogPost ->
            blogPost.header.timeReceived
        }
        txn.attach {
            _posts.addAll(posts)
            _isLoading.value = false
        }
    }

    @DatabaseExecutor
    private fun loadBlogPosts(txn: Transaction, groupId: GroupId): List<BlogPost> {
        return blogManager.getPostHeaders(txn, groupId).map { h ->
            getItem(txn, h)
        }
    }

    @UiExecutor
    private fun onBlogPostAdded(header: BlogPostHeader, local: Boolean) {
        runOnDbThreadWithTransaction(true) { txn ->
            val item = getItem(txn, header)

            LOG.error { "${item::class.java} ${item.text}" }

            txn.attach {
                _posts.add(item)
                _posts.sortByDescending { blogPost ->
                    blogPost.header.timeReceived
                }
                _isLoading.value = false
            }
        }
    }

    @UiExecutor
    private fun onBlogRemoved(id: GroupId) {
        _posts.removeIf { it.id == id }
    }

    @UiExecutor
    fun selectPost(item: BlogPost?) {
        _selectedPost.value = item
        if (item != null && !item.isRead) markPostsRead(listOf(item.id))
    }

    @UiExecutor
    fun createBlogPost(text: String) {
        val parentPost = selectedPost.value
        runOnDbThread {
            val author = identityManager.localAuthor
            val blog = blogManager.getPersonalBlog(author)
            if (parentPost == null) {
                val p = blogPostFactory.createBlogPost(
                    blog.id,
                    System.currentTimeMillis(),
                    null,
                    author,
                    text,
                )
                blogManager.addLocalPost(p)
            } else {
                val comment = text.takeIf { it.isNotBlank() }
                blogManager.addLocalComment(author, blog.id, comment, parentPost.header)
            }
        }
        _selectedPost.value = null
    }

    @UiExecutor
    fun markPostsRead(postIds: List<MessageId>) {
        runOnDbThread {
            postIds.forEach { id ->
                blogManager.setReadFlag(id, true)
            }
        }
    }

    @DatabaseExecutor
    private fun getItem(txn: Transaction, header: BlogPostHeader): BlogPost {
        return if (header is BlogCommentHeader) {
            val postHeader = BlogCommentItem.getBlogPostHeader(header)
            BlogCommentItem(header, postHeader, getPostText(txn, postHeader.id))
        } else {
            BlogPostItem(header, getPostText(txn, header.id))
        }
    }

    @DatabaseExecutor
    private fun getPostText(txn: Transaction, m: MessageId): String {
        return HtmlUtils.cleanArticle(blogManager.getPostText(txn, m))
    }
}
