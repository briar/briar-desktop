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

package org.briarproject.briar.desktop.introduction

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.introduction.IntroductionConstants.MAX_INTRODUCTION_TEXT_LENGTH
import org.briarproject.briar.api.introduction.IntroductionManager
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
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
    attachmentReader: AttachmentReader,
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
    private val _secondContact = mutableStateOf<ContactItem?>(null)
    private val _secondScreen = mutableStateOf(false)
    private val _introductionMessage = mutableStateOf("")

    val firstContact = _firstContact.asState()
    val secondContact = _secondContact.asState()
    val secondScreen = _secondScreen.asState()
    val introductionMessage = _introductionMessage.asState()

    val contactList = derivedStateOf {
        _contactList.filter {
            it.id != _firstContact.value?.id
        }.sortedByDescending { it.displayName }
    }

    fun setFirstContact(contactItem: ContactItem) {
        _firstContact.value = contactItem
        loadContacts()
        backToFirstScreen()
    }

    fun setSecondContact(contactItem: ContactItem) {
        _secondContact.value = contactItem
        _secondScreen.value = true
    }

    fun backToFirstScreen() {
        _secondScreen.value = false
        _introductionMessage.value = ""
    }

    fun setIntroductionMessage(msg: String) {
        _introductionMessage.value = msg.takeUtf8(MAX_INTRODUCTION_TEXT_LENGTH)
    }

    fun makeIntroduction() {
        val c1 = requireNotNull(_firstContact.value)
        val c2 = requireNotNull(_secondContact.value)
        // It's important not to send the empty string here as briar's MessageEncoder for introduction messages throws
        // an IllegalArgumentException in that case. It is however OK to pass null in this case.
        val msg = _introductionMessage.value.ifEmpty { null }

        runOnDbThread {
            val c1 = contactManager.getContact(c1.id)
            val c2 = contactManager.getContact(c2.id)
            introductionManager.makeIntroduction(c1, c2, msg)
        }
    }
}
