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

package org.briarproject.briar.desktop.ui

import org.briarproject.bramble.api.Multiset
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.desktop.conversation.ConversationMessagesReadEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import javax.inject.Inject

class MessageCounterImpl
@Inject
constructor(
    private val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val briarExecutors: BriarExecutors,
    eventBus: EventBus,
) : MessageCounter {

    private val messageCount = Multiset<ContactId>()

    private val listeners = mutableListOf<MessageCounterListener>()

    init {
        eventBus.addListener { e ->
            when (e) {
                is LifecycleEvent ->
                    if (e.lifecycleState == LifecycleManager.LifecycleState.RUNNING) {
                        briarExecutors.onDbThreadWithTransaction(true) { txn ->
                            val contacts = contactManager.getContacts(txn)
                            for (c in contacts) {
                                val unreadMessages = conversationManager.getGroupCount(txn, c.id).unreadCount
                                messageCount.addCount(c.id, unreadMessages)
                            }
                            txn.attach { informListeners() }
                        }
                    }

                is ConversationMessageReceivedEvent<*> -> {
                    messageCount.add(e.contactId)
                    informListeners()
                }

                is ConversationMessagesReadEvent -> {
                    messageCount.removeCount(e.contactId, e.count)
                    // if (messageCount < 0) messageCount = 0
                    // todo: test with unread messages on application start!
                }
            }
        }
    }

    override fun addListener(listener: MessageCounterListener) = listeners.add(listener)

    override fun removeListener(listener: MessageCounterListener) = listeners.remove(listener)

    private fun informListeners() = listeners.forEach { l ->
        l.invoke(MessageCounterData(messageCount.total, messageCount.unique))
    }

    private fun <T> Multiset<T>.removeCount(t: T, count: Int) =
        repeat(count) { remove(t) }

    private fun <T> Multiset<T>.addCount(t: T, count: Int) =
        repeat(count) { add(t) }
}
