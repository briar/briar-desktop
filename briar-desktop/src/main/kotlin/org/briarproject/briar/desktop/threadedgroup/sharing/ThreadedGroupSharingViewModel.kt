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

package org.briarproject.briar.desktop.threadedgroup.sharing

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.sharing.SharingConstants
import org.briarproject.briar.api.sharing.SharingManager
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.utils.StringUtils.takeUtf8
import org.briarproject.briar.desktop.viewmodel.asState

abstract class ThreadedGroupSharingViewModel(
    contactManager: ContactManager,
    authorManager: AuthorManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ContactsViewModel(
    contactManager,
    authorManager,
    conversationManager,
    connectionRegistry,
    briarExecutors,
    lifecycleManager,
    db,
    eventBus,
) {
    protected var _groupId: GroupId? = null

    protected val _sharingInfo = mutableStateOf(SharingInfo(0, 0))
    val sharingInfo = _sharingInfo.asState()

    protected val _sharingStatus = mutableStateOf(emptyMap<ContactId, SharingManager.SharingStatus>())
    protected val _shareableSelected = mutableStateOf(emptySet<ContactId>())
    protected val _sharingMessage = mutableStateOf("")

    open val deleteDialogCondition = false

    data class ShareableContactItem(val status: SharingManager.SharingStatus, val contactItem: ContactItem)

    val contactList = derivedStateOf {
        _contactList.mapNotNull {
            _sharingStatus.value[it.id]?.let { status ->
                ShareableContactItem(status, it)
            }
        }.sortedWith(
            // first all items that are SHAREABLE (false comes before true)
            // second non-case-sensitive, alphabetical order on displayName
            compareBy(
                { it.status != SharingManager.SharingStatus.SHAREABLE },
                { it.contactItem.displayName.lowercase(InternationalizationUtils.locale) }
            )
        )
    }

    val sharingMessage = _sharingMessage.asState()

    val buttonEnabled = derivedStateOf { _shareableSelected.value.isNotEmpty() }

    override fun onInit() {
        super.onInit()
        loadContacts()
        runOnDbThreadWithTransaction(true) { txn ->
            val contacts = contactManager.getContacts(txn)
            for (c in contacts) {
                println("${c.id.int} ${c.author.name} ${c.alias}")
            }
        }
    }

    @UiExecutor
    fun setGroupId(groupId: GroupId) {
        if (_groupId == groupId) return
        _groupId = groupId
        reload()
    }

    protected abstract fun reload()

    data class SharingInfo(val total: Int, val online: Int) {
        fun addContact(connected: Boolean) = copy(
            total = total + 1,
            online = if (connected) online + 1 else online
        )

        fun removeContact(connected: Boolean) = copy(
            total = total - 1,
            online = if (connected) online - 1 else online
        )

        fun updateContactConnected(connected: Boolean) = copy(
            total = total,
            online = if (connected) online + 1 else online - 1
        )
    }

    @UiExecutor
    fun isShareableSelected(shareable: ShareableContactItem) =
        _shareableSelected.value.contains(shareable.contactItem.id)

    @UiExecutor
    fun toggleShareable(shareable: ShareableContactItem) =
        if (isShareableSelected(shareable)) _shareableSelected.value -= shareable.contactItem.id
        else _shareableSelected.value += shareable.contactItem.id

    @UiExecutor
    fun setSharingMessage(message: String) {
        _sharingMessage.value = message.takeUtf8(SharingConstants.MAX_INVITATION_TEXT_LENGTH)
    }

    @UiExecutor
    abstract fun shareThreadedGroup()
}
