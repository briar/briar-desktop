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

package org.briarproject.briar.desktop.introduction

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.introduction.IntroductionConstants.MAX_INTRODUCTION_TEXT_LENGTH
import org.briarproject.briar.api.introduction.IntroductionManager
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.utils.StringUtils.takeUtf8
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class IntroductionViewModel
@Inject
constructor(
    private val introductionManager: IntroductionManager,
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

    private val _firstContact = mutableStateOf<ContactItem?>(null)
    private val _secondContactSelected = mutableStateOf(emptySet<ContactId>())

    private val _introductionPossible = mutableStateOf(emptyMap<ContactId, Boolean>())

    private val _introductionMessage = mutableStateOf("")
    val introductionMessage = _introductionMessage.asState()

    data class IntroductionContactItem(val introductionPossible: Boolean, val contactItem: ContactItem)

    val contactList = derivedStateOf {
        _contactList.filter {
            it.id != _firstContact.value?.id
        }.mapNotNull {
            _introductionPossible.value[it.id]?.let { possible ->
                IntroductionContactItem(possible, it)
            }
        }.sortedWith(
            // first all items where introduction is possible (false comes before true)
            // second non-case-sensitive, alphabetical order on displayName
            compareBy(
                { !it.introductionPossible },
                { it.contactItem.displayName.lowercase(InternationalizationUtils.locale) }
            )
        )
    }

    val buttonEnabled = derivedStateOf { _secondContactSelected.value.isNotEmpty() }

    override fun onInit() {
        super.onInit()
        loadContacts()
    }

    @UiExecutor
    fun setFirstContact(contactItem: ContactItem) {
        _firstContact.value = contactItem
        reset()
    }

    private fun reset() {
        _secondContactSelected.value = emptySet()
        _introductionMessage.value = ""

        val c1 = requireNotNull(_firstContact.value)
        runOnDbThreadWithTransaction(true) { txn ->
            val c1 = contactManager.getContact(txn, c1.id)
            val map = contactManager.getContacts(txn).associate { contact ->
                contact.id to introductionManager.canIntroduce(txn, c1, contact)
            }
            txn.attach {
                _introductionPossible.value = map
            }
        }
    }

    @UiExecutor
    fun isSecondContactSelected(item: IntroductionContactItem) =
        _secondContactSelected.value.contains(item.contactItem.id)

    @UiExecutor
    fun toggleSecondContact(item: IntroductionContactItem) =
        if (isSecondContactSelected(item)) _secondContactSelected.value -= item.contactItem.id
        else _secondContactSelected.value += item.contactItem.id

    @UiExecutor
    fun setIntroductionMessage(msg: String) {
        _introductionMessage.value = msg.takeUtf8(MAX_INTRODUCTION_TEXT_LENGTH)
    }

    @UiExecutor
    fun makeIntroduction() {
        val c1 = requireNotNull(_firstContact.value)
        val c2s = _secondContactSelected.value
        require(c2s.isNotEmpty())
        // It's important not to send the empty string here as briar's MessageEncoder for introduction messages throws
        // an IllegalArgumentException in that case. It is however OK to pass null in this case.
        val msg = _introductionMessage.value.ifEmpty { null }

        runOnDbThreadWithTransaction(false) { txn ->
            val c1 = contactManager.getContact(txn, c1.id)
            c2s.forEach { c2 ->
                val c2 = contactManager.getContact(txn, c2)
                introductionManager.makeIntroduction(txn, c1, c2, msg)
            }
        }
        reset()
    }
}
