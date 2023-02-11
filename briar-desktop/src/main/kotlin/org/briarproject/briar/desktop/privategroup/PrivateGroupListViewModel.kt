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

package org.briarproject.briar.desktop.privategroup

import androidx.compose.runtime.mutableStateListOf
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.ClientId
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.client.PostHeader
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.api.privategroup.event.GroupMessageAddedEvent
import org.briarproject.briar.desktop.forums.ThreadedConversationViewModel
import org.briarproject.briar.desktop.group.GroupListViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import javax.inject.Inject

class PrivateGroupListViewModel
@Inject constructor(
    private val privateGroupManager: PrivateGroupManager,
    threadViewModel: ThreadedConversationViewModel, // todo: subclass
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : GroupListViewModel<PrivateGroupItem>(threadViewModel, briarExecutors, lifecycleManager, db, eventBus) {

    override val clientId: ClientId = ForumManager.CLIENT_ID

    override val _groupList = mutableStateListOf<PrivateGroupItem>()

    override fun eventOccurred(e: Event) {
        super.eventOccurred(e)
        when (e) {
            is GroupMessageAddedEvent -> {
                updateItem(e.groupId) { it.updateOnPostReceived(e.header) }
            }

            // TODO
            /*is ForumPostReadEvent -> {
                updateItem(e.groupId) { it.updateOnPostsRead(e.numMarkedRead) }
            }*/
        }
    }

    override fun createGroupItem(txn: Transaction, id: GroupId) = PrivateGroupItem(
        privateGroup = privateGroupManager.getPrivateGroup(txn, id),
        groupCount = privateGroupManager.getGroupCount(txn, id),
    )

    fun createPrivateGroup(name: String) = runOnDbThread {
        TODO()
        //privateGroupManager.addForum(name)
    }

    override fun loadGroups(txn: Transaction) =
        privateGroupManager.getPrivateGroups(txn).map { privateGroup ->
            PrivateGroupItem(
                privateGroup = privateGroup,
                groupCount = privateGroupManager.getGroupCount(txn, privateGroup.id),
            )
        }

    override fun addOwnMessage(header: PostHeader) {
        selectedGroupId.value?.let { id -> updateItem(id) { it.updateOnPostReceived(header) } }
    }

    private fun updateItem(groupId: GroupId, update: (PrivateGroupItem) -> PrivateGroupItem) =
        _groupList.replaceFirst({ it.id == groupId }, update)

    override fun removeItem(groupId: GroupId) =
        _groupList.removeFirst { it.id == groupId }
}
