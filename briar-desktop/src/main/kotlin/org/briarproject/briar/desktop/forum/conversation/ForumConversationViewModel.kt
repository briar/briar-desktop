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

package org.briarproject.briar.desktop.forum.conversation

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
import org.briarproject.bramble.api.sync.ClientId
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.system.Clock
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.api.forum.event.ForumPostReceivedEvent
import org.briarproject.briar.desktop.forum.ForumItem
import org.briarproject.briar.desktop.forum.sharing.ForumSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.conversation.ThreadedConversationViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import java.lang.Long.max
import javax.inject.Inject

class ForumConversationViewModel @Inject constructor(
    forumSharingViewModel: ForumSharingViewModel,
    private val forumManager: ForumManager,
    private val identityManager: IdentityManager,
    private val clock: Clock,
    @CryptoExecutor private val cryptoDispatcher: CoroutineDispatcher,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ThreadedConversationViewModel(
    forumSharingViewModel,
    briarExecutors,
    lifecycleManager,
    db,
    eventBus
) {

    override val clientId: ClientId = ForumManager.CLIENT_ID

    @UiExecutor
    override fun eventOccurred(e: Event) {
        if (e is ForumPostReceivedEvent) {
            if (e.groupId == groupItem.value?.id) {
                val item = ForumPostItem(e.header, e.text)
                addItem(item)
            }
        }
    }

    override fun loadThreadItems(txn: Transaction, groupId: GroupId) =
        forumManager.getPostHeaders(txn, groupId).map { header ->
            ForumPostItem(header, forumManager.getPostText(txn, header.id))
        }

    @UiExecutor
    @OptIn(DelicateCoroutinesApi::class)
    override fun createThreadItem(text: String) = GlobalScope.launch {
        val groupId = groupItem.value?.id ?: return@launch
        val parentId = selectedThreadItem.value?.id
        val author = runOnDbThreadWithTransaction<LocalAuthor>(false) { txn ->
            identityManager.getLocalAuthor(txn)
        }
        val count = runOnDbThreadWithTransaction<MessageTracker.GroupCount>(false) { txn ->
            forumManager.getGroupCount(txn, groupId)
        }
        val timestamp = max(count.latestMsgTime + 1, clock.currentTimeMillis())
        val post = withContext(cryptoDispatcher) {
            forumManager.createLocalPost(groupId, text, timestamp, parentId, author)
        }
        runOnDbThreadWithTransaction(false) { txn ->
            val header = forumManager.addLocalPost(txn, post)
            txn.attach {
                val item = ForumPostItem(header, text)
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
    override fun markThreadItemRead(txn: Transaction, groupId: GroupId, id: MessageId) =
        forumManager.setReadFlag(txn, groupId, id, true)

    @UiExecutor
    override fun deleteGroup() {
        groupItem.value?.let {
            runOnDbThread { forumManager.removeForum((it as ForumItem).forum) }
        }
    }
}
