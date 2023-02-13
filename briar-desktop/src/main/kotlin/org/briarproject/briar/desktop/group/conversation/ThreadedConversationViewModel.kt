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

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Job
import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.client.PostHeader
import org.briarproject.briar.client.MessageTreeImpl
import org.briarproject.briar.desktop.forums.ForumPostReadEvent
import org.briarproject.briar.desktop.forums.sharing.ForumSharingViewModel
import org.briarproject.briar.desktop.group.GroupItem
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState

abstract class ThreadedConversationViewModel(
    val forumSharingViewModel: ForumSharingViewModel,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _groupItem = mutableStateOf<GroupItem?>(null)
    val groupItem = _groupItem.asState()

    protected lateinit var onThreadItemAdded: (header: PostHeader) -> Unit

    private val _state = mutableStateOf<ThreadedConversationScreenState>(Loading)
    val state = _state.asState()

    private val _selectedThreadItem = mutableStateOf<ThreadItem?>(null)
    val selectedThreadItem = _selectedThreadItem.asState()

    @UiExecutor
    fun setGroupItem(groupItem: GroupItem, onThreadItemAdded: (header: PostHeader) -> Unit) {
        this.onThreadItemAdded = onThreadItemAdded
        _groupItem.value = groupItem
        _selectedThreadItem.value = null
        forumSharingViewModel.setGroupId(groupItem.id)
        loadThreadItems(groupItem.id)
    }

    override fun onInit() {
        super.onInit()
        forumSharingViewModel.onEnterComposition()
    }

    override fun onCleared() {
        super.onCleared()
        forumSharingViewModel.onExitComposition()
    }

    protected abstract fun loadThreadItems(txn: Transaction, groupId: GroupId): List<ThreadItem>

    private fun loadThreadItems(groupId: GroupId) {
        _state.value = Loading
        runOnDbThreadWithTransaction(true) { txn ->
            val items = loadThreadItems(txn, groupId)
            val tree = MessageTreeImpl<ThreadItem>().apply { add(items) }
            txn.attach {
                _state.value = Loaded(tree)
            }
        }
    }

    @UiExecutor
    fun selectThreadItem(item: ThreadItem?) {
        _selectedThreadItem.value = item
        if (item != null && !item.isRead) markThreadItemsRead(listOf(item.id))
    }

    @UiExecutor
    abstract fun createThreadItem(text: String): Job

    @UiExecutor
    protected fun addItem(item: ThreadItem, scrollTo: MessageId? = null) {
        // If items haven't loaded, we need to wait until they have.
        // Since this was a R/W DB transaction, the load will pick up this item.
        val tree = (state.value as? Loaded)?.messageTree ?: return
        tree.add(item)
        _state.value = Loaded(tree, scrollTo)
    }

    @DatabaseExecutor
    abstract fun markThreadItemRead(groupId: GroupId, id: MessageId)

    @UiExecutor
    fun markThreadItemsRead(ids: List<MessageId>) {
        // TODO messageTree.get(id) would be nice, but not in briar-core
        val readIds = (state.value as? Loaded)?.posts?.filter { item ->
            !item.isRead && ids.contains(item.id)
        }?.map { item ->
            item.isRead = true
            item.id
        } ?: emptyList()

        val groupId = _groupItem.value?.id
        if (readIds.isNotEmpty() && groupId != null) {
            runOnDbThread {
                readIds.forEach { id ->
                    markThreadItemRead(groupId, id)
                }
            }
            // we don't attach this to the transaction that actually changes the DB,
            // but that should be fine for this purpose of just decrementing a counter
            eventBus.broadcast(ForumPostReadEvent(groupId, readIds.size))
            // TODO replace immutable ThreadItems instead to avoid recomposing whole list
            val messageTree = (state.value as? Loaded)?.messageTree ?: return
            _state.value = Loaded(messageTree)
        }
    }

    abstract fun deleteGroup()
}
