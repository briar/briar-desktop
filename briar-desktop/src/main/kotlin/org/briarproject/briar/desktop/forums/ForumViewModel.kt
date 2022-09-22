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

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.briarproject.bramble.api.crypto.CryptoExecutor
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.sync.event.GroupAddedEvent
import org.briarproject.bramble.api.sync.event.GroupRemovedEvent
import org.briarproject.bramble.api.system.Clock
import org.briarproject.briar.api.client.MessageTracker.GroupCount
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.client.MessageTreeImpl
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import java.lang.Long.max
import javax.inject.Inject

sealed class PostsState
object Loading : PostsState()
class Loaded(
    val messageTree: MessageTreeImpl<ThreadItem>,
    val scrollTo: MessageId? = null,
) : PostsState() {
    val posts: MutableList<ThreadItem> get() = messageTree.depthFirstOrder()
}

class ForumViewModel @Inject constructor(
    private val forumManager: ForumManager,
    private val identityManager: IdentityManager,
    private val clock: Clock,
    @CryptoExecutor private val cryptoDispatcher: CoroutineDispatcher,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    private val _fullForumList = mutableStateListOf<ForumItem>()
    val forumList = derivedStateOf {
        val filter = _filterBy.value
        _fullForumList.filter { item ->
            item.name.contains(filter, ignoreCase = true)
        }.sortedByDescending { it.timestamp }
    }

    private val _selectedGroupItem = mutableStateOf<GroupItem?>(null)
    val selectedGroupItem = _selectedGroupItem.asState()

    private val _filterBy = mutableStateOf("")
    val filterBy = _filterBy.asState()

    private val _posts = mutableStateOf<PostsState>(Loading)
    val posts = _posts.asState()

    override fun onInit() {
        super.onInit()
        loadGroups()
    }

    override fun eventOccurred(e: Event) {
        if (e is GroupAddedEvent) {
            if (e.group.clientId == ForumManager.CLIENT_ID) loadGroups()
        } else if (e is GroupRemovedEvent) {
            if (e.group.clientId == ForumManager.CLIENT_ID) {
                loadGroups()
                if (selectedGroupItem.value?.id == e.group.id) _selectedGroupItem.value = null
            }
        }
    }

    fun createForum(name: String) {
        forumManager.addForum(name)
    }

    private fun loadGroups() {
        runOnDbThreadWithTransaction(true) { txn ->
            val list = forumManager.getForums(txn).map { forums ->
                ForumItem(
                    forum = forums,
                    groupCount = forumManager.getGroupCount(txn, forums.id),
                )
            }
            txn.attach {
                _fullForumList.clearAndAddAll(list)
            }
        }
    }

    fun selectGroup(groupItem: GroupItem) {
        _selectedGroupItem.value = groupItem
        loadPosts(groupItem.id)
    }

    fun isSelected(groupId: GroupId) = _selectedGroupItem.value?.id == groupId

    private fun loadPosts(groupId: GroupId) {
        _posts.value = Loading
        runOnDbThreadWithTransaction(true) { txn ->
            val items = forumManager.getPostHeaders(txn, groupId).map { header ->
                ForumPostItem(header, forumManager.getPostText(txn, header.id))
            }
            val tree = MessageTreeImpl<ThreadItem>().apply { add(items) }
            txn.attach {
                _posts.value = Loaded(tree)
            }
        }
    }

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun createPost(groupItem: GroupItem, text: String, parentId: MessageId?) = GlobalScope.launch {
        val author = runOnDbThreadWithTransaction<LocalAuthor>(false) { txn ->
            identityManager.getLocalAuthor(txn)
        }
        val count = runOnDbThreadWithTransaction<GroupCount>(false) { txn ->
            forumManager.getGroupCount(txn, groupItem.id)
        }
        val timestamp = max(count.latestMsgTime + 1, clock.currentTimeMillis())
        val post = withContext(cryptoDispatcher) {
            forumManager.createLocalPost(groupItem.id, text, timestamp, parentId, author)
        }
        runOnDbThreadWithTransaction(false) { txn ->
            val header = forumManager.addLocalPost(txn, post)
            txn.attach {
                val item = ForumPostItem(header, text)
                addItem(item, item.id)
            }
        }
    }

    @UiExecutor
    private fun addItem(item: ForumPostItem, scrollTo: MessageId? = null) {
        // If items haven't loaded, we need to wait until they have.
        // Since this was a R/W DB transaction, the load will pick up this item.
        val tree = (posts.value as? Loaded)?.messageTree ?: return
        tree.add(item)
        _posts.value = Loaded(tree, scrollTo)
    }

    fun deleteGroup(groupItem: GroupItem) {
        forumManager.removeForum((groupItem as ForumItem).forum)
    }
}
