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

package org.briarproject.briar.desktop.privategroup.conversation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.briarproject.bramble.api.crypto.CryptoExecutor
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.system.Clock
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.privategroup.GroupMessageFactory
import org.briarproject.briar.api.privategroup.JoinMessageHeader
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.desktop.privategroup.sharing.PrivateGroupSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.conversation.ThreadedConversationViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import java.lang.Long.max
import javax.inject.Inject

class PrivateGroupConversationViewModel @Inject constructor(
    sharingViewModel: PrivateGroupSharingViewModel,
    private val privateGroupManager: PrivateGroupManager,
    private val privateGroupMessageFactory: GroupMessageFactory,
    private val identityManager: IdentityManager,
    private val clock: Clock,
    @CryptoExecutor private val cryptoDispatcher: CoroutineDispatcher,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ThreadedConversationViewModel(
    sharingViewModel,
    briarExecutors,
    lifecycleManager,
    db,
    eventBus
) {

    @UiExecutor
    override fun eventOccurred(e: Event) {
        // todo: handle incoming messages
        /*if (e is ForumPostReceivedEvent) {
            if (e.groupId == groupItem.value?.id) {
                val item = ForumPostItem(e.header, e.text)
                addItem(item, null)
            }
        }*/
    }

    override fun loadThreadItems(txn: Transaction, groupId: GroupId) =
        privateGroupManager.getHeaders(txn, groupId).map { header ->
            PrivateGroupMessageItem(
                header,
                if (header !is JoinMessageHeader) privateGroupManager.getMessageText(txn, header.id)
                else "" // todo
            )
        }

    @UiExecutor
    @OptIn(DelicateCoroutinesApi::class)
    override fun createThreadItem(text: String) = GlobalScope.launch {
        val groupId = groupItem.value?.id ?: return@launch
        val parentId = selectedThreadItem.value?.id
        val author = runOnDbThreadWithTransaction<LocalAuthor>(false) { txn ->
            identityManager.getLocalAuthor(txn)
        }
        val previousMsgId = runOnDbThread<MessageId> { privateGroupManager.getPreviousMsgId(groupId) }
        val count = runOnDbThreadWithTransaction<MessageTracker.GroupCount>(false) { txn ->
            privateGroupManager.getGroupCount(txn, groupId)
        }
        val timestamp = max(count.latestMsgTime + 1, clock.currentTimeMillis())
        val post = withContext(cryptoDispatcher) {
            privateGroupMessageFactory.createGroupMessage(groupId, timestamp, parentId, author, text, previousMsgId)
        }
        runOnDbThreadWithTransaction(false) { txn ->
            val header = privateGroupManager.addLocalMessage(txn, post)
            txn.attach {
                val item = PrivateGroupMessageItem(header, text)
                addItem(item, scrollTo = item.id)
                onThreadItemLocallyAdded(header)
                // unselect post that we just replied to
                if (parentId != null) {
                    selectThreadItem(null)
                }
            }
        }
    }

    @DatabaseExecutor
    override fun markThreadItemRead(groupId: GroupId, id: MessageId) =
        privateGroupManager.setReadFlag(groupId, id, true)

    override fun deleteGroup() {
        groupItem.value?.let { privateGroupManager.removePrivateGroup(it.id) }
    }
}
