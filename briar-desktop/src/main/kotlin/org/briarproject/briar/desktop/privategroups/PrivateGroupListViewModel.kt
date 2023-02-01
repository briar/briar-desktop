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

package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import javax.inject.Inject

class PrivateGroupListViewModel
@Inject
constructor(
    private val privateGroupManager: PrivateGroupManager,
    val conversationManager: ConversationManager,
    val connectionRegistry: ConnectionRegistry,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    private val _fullPrivateGroupList = mutableStateListOf<PrivateGroupItem>()

    val privateGroupList: List<PrivateGroupItem> = _fullPrivateGroupList

    private fun loadPrivateGroups() {
        val privateGroupList = mutableListOf<PrivateGroupItem>()
        runOnDbThreadWithTransaction(true) { txn ->
            privateGroupList.addAll(
                privateGroupManager.getPrivateGroups(txn).map { privateGroup ->
                    PrivateGroupItem(
                        privateGroup,
                        privateGroupManager.getGroupCount(txn, privateGroup.id),
                    )
                }
            )
            txn.attach {
                _fullPrivateGroupList.clearAndAddAll(privateGroupList)
            }
        }
    }

    override fun onInit() {
        super.onInit()
        loadPrivateGroups()
    }

    private val _selectedContactId = mutableStateOf<GroupId?>(null)

    val selectedPrivateGroupId: State<GroupId?> = _selectedContactId

    fun selectPrivateGroup(privateGroupId: GroupId) {
        _selectedContactId.value = privateGroupId
    }

    fun isSelected(privateGroupId: GroupId) = _selectedContactId.value == privateGroupId

    override fun eventOccurred(e: Event) {
        // TODO
    }
}
