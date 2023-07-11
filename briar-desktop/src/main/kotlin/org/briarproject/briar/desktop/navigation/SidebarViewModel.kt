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

package org.briarproject.briar.desktop.navigation

import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.bramble.api.mailbox.event.MailboxProblemEvent
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.ui.MessageCounter
import org.briarproject.briar.desktop.ui.MessageCounterData
import org.briarproject.briar.desktop.ui.MessageCounterDataType.Blog
import org.briarproject.briar.desktop.ui.MessageCounterDataType.Forum
import org.briarproject.briar.desktop.ui.MessageCounterDataType.PrivateGroup
import org.briarproject.briar.desktop.ui.MessageCounterDataType.PrivateMessage
import org.briarproject.briar.desktop.ui.UiMode
import org.briarproject.briar.desktop.viewmodel.ViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import org.briarproject.briar.desktop.viewmodel.update
import javax.inject.Inject

class SidebarViewModel
@Inject
constructor(
    private val identityManager: IdentityManager,
    private val messageCounter: MessageCounter,
    private val eventBus: EventBus,
) : EventListener, ViewModel() {
    private var listensToEventBus = false

    override fun onInit() {
        super.onInit()
        if (!listensToEventBus) {
            eventBus.addListener(this)
            listensToEventBus = true
        }
        loadAccountInfo()
        messageCounter.addListener(this::onMessageCounterUpdated)
    }

    override fun onCleared() {
        super.onCleared()
        messageCounter.removeListener(this::onMessageCounterUpdated)
    }

    override fun eventOccurred(e: Event) {
        if (e is MailboxProblemEvent) _mailboxProblem.value = true
    }

    private fun onMessageCounterUpdated(data: MessageCounterData) {
        val (type, count) = data
        when (type) {
            PrivateMessage -> _messageCount.update { copy(privateMessages = count) }
            Forum -> _messageCount.update { copy(forumPosts = count) }
            PrivateGroup -> _messageCount.update { copy(privateGroupMessages = count) }
            Blog -> _messageCount.update { copy(blogPosts = count) }
        }
    }

    private val _uiMode = mutableStateOf(UiMode.CONTACTS)
    private val _account = mutableStateOf<LocalAuthor?>(null)
    private val _mailboxProblem = mutableStateOf(false)

    private val _messageCount = mutableStateOf(MessageCount())

    val uiMode = _uiMode.asState()
    val account = _account.asState()
    val mailboxProblem = _mailboxProblem.asState()

    val messageCount = _messageCount.asState()

    @UiExecutor
    fun setUiMode(uiMode: UiMode) {
        _uiMode.value = uiMode
        if (uiMode == UiMode.MAILBOX) _mailboxProblem.value = false
    }

    private fun loadAccountInfo() {
        _account.value = identityManager.localAuthor
    }

    data class MessageCount(
        val privateMessages: Int = 0,
        val forumPosts: Int = 0,
        val privateGroupMessages: Int = 0,
        val blogPosts: Int = 0,
    )
}
