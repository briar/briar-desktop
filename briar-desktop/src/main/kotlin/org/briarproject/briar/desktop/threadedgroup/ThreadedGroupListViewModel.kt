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

package org.briarproject.briar.desktop.threadedgroup

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import io.github.oshai.kotlinlogging.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.ClientId
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.event.GroupAddedEvent
import org.briarproject.bramble.api.sync.event.GroupRemovedEvent
import org.briarproject.briar.api.client.PostHeader
import org.briarproject.briar.desktop.threadedgroup.conversation.ThreadedConversationViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState

abstract class ThreadedGroupListViewModel<VM : ThreadedConversationViewModel, GroupItem : ThreadedGroupItem>(
    val threadViewModel: VM,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    protected abstract val clientId: ClientId

    protected abstract val _groupList: MutableList<GroupItem>
    val list = derivedStateOf {
        val filter = _filterBy.value
        _groupList.filter { item ->
            item.name.contains(filter, ignoreCase = true)
        }.sortedByDescending { it.timestamp }
    }

    val noGroupsYet = derivedStateOf { _groupList.isEmpty() }

    private val _selectedGroupId = mutableStateOf<GroupId?>(null)
    val selectedGroupId = derivedStateOf {
        // reset selected group item to null if not part of list after filtering
        val groupId = _selectedGroupId.value
        if (groupId == null || list.value.any { it.id == groupId }) {
            groupId
        } else {
            _selectedGroupId.value = null
            null
        }
    }

    private val _filterBy = mutableStateOf("")
    val filterBy = _filterBy.asState()

    override fun onInit() {
        super.onInit()
        // since the threadViewModel is tightly coupled to the ForumViewModel
        // and not injected using the usual `viewModel()` approach,
        // we have to manually call the functions for (de)initialization
        threadViewModel.onEnterComposition()
        loadGroups()
    }

    override fun onCleared() {
        super.onCleared()
        threadViewModel.onExitComposition()
    }

    override fun eventOccurred(e: Event) {
        when {
            e is GroupAddedEvent && e.group.clientId == clientId ->
                onGroupAdded(e.group.id)

            e is GroupRemovedEvent && e.group.clientId == clientId -> {
                removeItem(e.group.id)
                if (_selectedGroupId.value == e.group.id) _selectedGroupId.value = null
            }
        }
    }

    @DatabaseExecutor
    protected abstract fun createGroupItem(txn: Transaction, id: GroupId): GroupItem

    private fun onGroupAdded(id: GroupId) = runOnDbThreadWithTransaction(true) { txn ->
        val item = createGroupItem(txn, id)
        txn.attach {
            addItem(item)
        }
    }

    @DatabaseExecutor
    protected abstract fun loadGroups(txn: Transaction): List<GroupItem>

    private fun loadGroups() = runOnDbThreadWithTransaction(true) { txn ->
        val list = loadGroups(txn)
        txn.attach {
            _groupList.clearAndAddAll(list)
        }
    }

    abstract fun createGroup(name: String)

    protected abstract fun addOwnMessage(header: PostHeader)

    fun selectGroup(threadedGroupItem: ThreadedGroupItem) {
        if (_selectedGroupId.value == threadedGroupItem.id) return
        _selectedGroupId.value = threadedGroupItem.id
        threadViewModel.setGroupItem(threadedGroupItem, this::addOwnMessage)
    }

    fun isSelected(groupId: GroupId) = _selectedGroupId.value == groupId

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
    }

    private fun addItem(groupItem: GroupItem) = _groupList.add(groupItem)

    protected abstract fun removeItem(groupId: GroupId): Boolean
}
