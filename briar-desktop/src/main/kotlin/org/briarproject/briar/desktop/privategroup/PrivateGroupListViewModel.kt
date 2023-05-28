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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.briarproject.bramble.api.crypto.CryptoExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.ClientId
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.system.Clock
import org.briarproject.briar.api.client.PostHeader
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.privategroup.GroupMessageFactory
import org.briarproject.briar.api.privategroup.PrivateGroupFactory
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.api.privategroup.event.GroupDissolvedEvent
import org.briarproject.briar.api.privategroup.event.GroupMessageAddedEvent
import org.briarproject.briar.desktop.privategroup.conversation.PrivateGroupConversationViewModel
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupListViewModel
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupMessageReadEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import javax.inject.Inject

class PrivateGroupListViewModel
@Inject constructor(
    private val clock: Clock,
    private val authorManager: AuthorManager,
    private val identityManager: IdentityManager,
    private val privateGroupManager: PrivateGroupManager,
    private val privateGroupFactory: PrivateGroupFactory,
    private val privateGroupMessageFactory: GroupMessageFactory,
    @CryptoExecutor private val cryptoDispatcher: CoroutineDispatcher,
    threadViewModel: PrivateGroupConversationViewModel,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ThreadedGroupListViewModel<PrivateGroupConversationViewModel, PrivateGroupItem>(
    threadViewModel, briarExecutors, lifecycleManager, db, eventBus
) {

    override val clientId: ClientId = PrivateGroupManager.CLIENT_ID

    override val _groupList = mutableStateListOf<PrivateGroupItem>()

    override fun eventOccurred(e: Event) {
        super.eventOccurred(e)
        when (e) {
            is GroupMessageAddedEvent -> {
                updateItem(e.groupId) { it.updateOnMessageReceived(e.header) }
            }

            is ThreadedGroupMessageReadEvent -> {
                if (e.clientId != clientId) return
                updateItem(e.groupId) { it.updateOnMessagesRead(e.numMarkedRead) }
            }

            is GroupDissolvedEvent -> {
                updateItem(e.groupId) { it.updateOnDissolve() }
            }
        }
    }

    override fun createGroupItem(txn: Transaction, id: GroupId): PrivateGroupItem {
        val privateGroup = privateGroupManager.getPrivateGroup(txn, id)
        return PrivateGroupItem(
            privateGroup = privateGroup,
            creatorInfo = authorManager.getAuthorInfo(txn, privateGroup.creator.id),
            isDissolved = privateGroupManager.isDissolved(txn, id),
            groupCount = privateGroupManager.getGroupCount(txn, id),
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun createGroup(name: String) {
        GlobalScope.launch {
            val author = runOnDbThread<LocalAuthor> { identityManager.localAuthor }
            val (group, joinMsg) = withContext(cryptoDispatcher) {
                val group = privateGroupFactory.createPrivateGroup(name, author)
                val joinMsg = privateGroupMessageFactory.createJoinMessage(
                    group.id, clock.currentTimeMillis(), author
                )
                return@withContext Pair(group, joinMsg)
            }
            runOnDbThread { privateGroupManager.addPrivateGroup(group, joinMsg, true) }
        }
    }

    override fun loadGroups(txn: Transaction) =
        privateGroupManager.getPrivateGroups(txn).map { privateGroup ->
            PrivateGroupItem(
                privateGroup = privateGroup,
                isDissolved = privateGroupManager.isDissolved(txn, privateGroup.id),
                creatorInfo = authorManager.getAuthorInfo(txn, privateGroup.creator.id),
                groupCount = privateGroupManager.getGroupCount(txn, privateGroup.id),
            )
        }

    override fun addOwnMessage(header: PostHeader) {
        // no-op since GroupMessageAddedEvent is also sent on locally added message
    }

    private fun updateItem(groupId: GroupId, update: (PrivateGroupItem) -> PrivateGroupItem) =
        _groupList.replaceFirst({ it.id == groupId }, update)

    override fun removeItem(groupId: GroupId) =
        _groupList.removeFirst { it.id == groupId }
}
