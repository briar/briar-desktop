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

package org.briarproject.briar.desktop.ui

import mu.KotlinLogging
import org.briarproject.bramble.api.Multiset
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.event.GroupRemovedEvent
import org.briarproject.briar.api.blog.BlogManager
import org.briarproject.briar.api.blog.event.BlogPostAddedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.api.forum.event.ForumPostReceivedEvent
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.api.privategroup.event.GroupMessageAddedEvent
import org.briarproject.briar.desktop.blog.BlogPostsReadEvent
import org.briarproject.briar.desktop.conversation.ConversationMessagesReadEvent
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupMessageReadEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.ui.MessageCounterDataType.Blog
import org.briarproject.briar.desktop.ui.MessageCounterDataType.Forum
import org.briarproject.briar.desktop.ui.MessageCounterDataType.PrivateGroup
import org.briarproject.briar.desktop.ui.MessageCounterDataType.PrivateMessage
import org.briarproject.briar.desktop.utils.KLoggerUtils.e
import org.briarproject.briar.desktop.utils.getRandomId
import javax.inject.Inject

class MessageCounterImpl
@Inject
constructor(
    private val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val forumManager: ForumManager,
    private val privateGroupManager: PrivateGroupManager,
    private val blogManager: BlogManager,
    private val briarExecutors: BriarExecutors,
    eventBus: EventBus,
) : MessageCounter {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    @UiExecutor
    private val countPrivateMessages = Multiset<ContactId>()

    @UiExecutor
    private val countForumPosts = Multiset<GroupId>()

    @UiExecutor
    private val countPrivateGroupMessages = Multiset<GroupId>()

    @UiExecutor
    private var countBlogPosts: Int = 0
    private val fakeBlogGroupId: GroupId = GroupId(getRandomId())

    private val listeners = mutableListOf<MessageCounterListener>()

    init {
        eventBus.addListener { e ->
            when (e) {
                is LifecycleEvent ->
                    if (e.lifecycleState == RUNNING) {
                        briarExecutors.onDbThreadWithTransaction(true) { txn ->
                            val countPrivateMessagesMap = contactManager.getContacts(txn).associate { c ->
                                c.id to conversationManager.getGroupCount(txn, c.id).unreadCount
                            }
                            val countForumPostsMap = forumManager.getForums(txn).associate { f ->
                                f.id to forumManager.getGroupCount(txn, f.id).unreadCount
                            }
                            val countPrivateGroupMessageMap = privateGroupManager.getPrivateGroups(txn).associate { g ->
                                g.id to privateGroupManager.getGroupCount(txn, g.id).unreadCount
                            }
                            val blogPosts = blogManager.getBlogIds(txn).flatMap { b ->
                                blogManager.getPostHeaders(txn, b)
                            }
                            txn.attach {
                                countPrivateMessagesMap.forEach { (id, count) ->
                                    countPrivateMessages.addCount(id, count)
                                }
                                countForumPostsMap.forEach { (id, count) ->
                                    countForumPosts.addCount(id, count)
                                }
                                countPrivateGroupMessageMap.forEach { (id, count) ->
                                    countPrivateGroupMessages.addCount(id, count)
                                }
                                countBlogPosts = blogPosts.count { post -> !post.isRead }
                                informListeners(PrivateMessage, true)
                                informListeners(Forum, true)
                                informListeners(PrivateGroup, true)
                                informListeners(Blog, true)
                            }
                        }
                    }

                is ConversationMessageReceivedEvent<*> -> {
                    countPrivateMessages.add(e.contactId)
                    informListeners(PrivateMessage, true)
                }

                is ConversationMessagesReadEvent -> {
                    try {
                        countPrivateMessages.removeCount(e.contactId, e.count)
                    } catch (e: NoSuchElementException) {
                        LOG.e(e) {
                            "inconsistent state in MessageCounterImpl.countPrivateMessages: " +
                                "trying to remove non-existing element"
                        }
                    }
                    informListeners(PrivateMessage, false)
                }

                is ContactRemovedEvent -> {
                    countPrivateMessages.removeAll(e.contactId)
                    informListeners(PrivateMessage, false)
                }

                is ForumPostReceivedEvent -> {
                    countForumPosts.add(e.groupId)
                    informListeners(Forum, true)
                }

                is GroupMessageAddedEvent -> {
                    if (e.isLocal) return@addListener

                    countPrivateGroupMessages.add(e.groupId)
                    informListeners(PrivateGroup, true)
                }

                is BlogPostAddedEvent -> {
                    if (e.isLocal) return@addListener

                    countBlogPosts++
                    informListeners(Blog, true)
                }

                is ThreadedGroupMessageReadEvent -> {
                    try {
                        when (e.clientId) {
                            ForumManager.CLIENT_ID -> {
                                countForumPosts.removeCount(e.groupId, e.numMarkedRead)
                                informListeners(Forum, false)
                            }

                            PrivateGroupManager.CLIENT_ID -> {
                                countPrivateGroupMessages.removeCount(e.groupId, e.numMarkedRead)
                                informListeners(PrivateGroup, false)
                            }
                        }
                    } catch (e: NoSuchElementException) {
                        LOG.e(e) {
                            "inconsistent state in MessageCounterImpl: " +
                                "trying to remove non-existing element"
                        }
                    }
                }

                is BlogPostsReadEvent -> {
                    countBlogPosts -= e.numPostsMarkedRead
                    informListeners(Blog, false)
                }

                is GroupRemovedEvent -> {
                    when (e.group.clientId) {
                        ForumManager.CLIENT_ID -> {
                            countForumPosts.removeAll(e.group.id)
                            informListeners(Forum, false)
                        }

                        PrivateGroupManager.CLIENT_ID -> {
                            countPrivateGroupMessages.removeAll(e.group.id)
                            informListeners(PrivateGroup, false)
                        }
                    }
                }
            }
        }
    }

    override fun addListener(listener: MessageCounterListener) = listeners.add(listener)

    override fun removeListener(listener: MessageCounterListener) = listeners.remove(listener)

    private fun informListeners(type: MessageCounterDataType, increment: Boolean) = listeners.forEach { l ->
        val groupCount = when (type) {
            PrivateMessage -> countPrivateMessages
            Forum -> countForumPosts
            PrivateGroup -> countPrivateGroupMessages
            Blog -> Multiset<GroupId>().apply { addCount(fakeBlogGroupId, countBlogPosts) }
        }
        l.invoke(MessageCounterData(type, groupCount.total, groupCount.unique, increment))
    }

    private fun <T> Multiset<T>.removeCount(t: T, count: Int) =
        repeat(count) { remove(t) }

    private fun <T> Multiset<T>.addCount(t: T, count: Int) =
        repeat(count) { add(t) }
}
