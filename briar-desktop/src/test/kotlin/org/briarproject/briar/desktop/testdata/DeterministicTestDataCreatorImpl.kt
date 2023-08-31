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

package org.briarproject.briar.desktop.testdata

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import mu.KotlinLogging
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.client.ClientHelper
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.crypto.SecretKey
import org.briarproject.bramble.api.db.DatabaseComponent
import org.briarproject.bramble.api.db.DbException
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.identity.AuthorFactory
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.plugin.BluetoothConstants
import org.briarproject.bramble.api.plugin.LanTcpConstants
import org.briarproject.bramble.api.plugin.TorConstants
import org.briarproject.bramble.api.plugin.TransportId
import org.briarproject.bramble.api.properties.TransportProperties
import org.briarproject.bramble.api.properties.TransportPropertyManager
import org.briarproject.bramble.api.sync.Group
import org.briarproject.bramble.api.sync.GroupFactory
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.system.Clock
import org.briarproject.briar.api.autodelete.AutoDeleteConstants
import org.briarproject.briar.api.avatar.AvatarManager
import org.briarproject.briar.api.avatar.AvatarMessageEncoder
import org.briarproject.briar.api.blog.BlogFactory
import org.briarproject.briar.api.blog.BlogManager
import org.briarproject.briar.api.blog.BlogPostFactory
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.forum.ForumFactory
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.privategroup.GroupMessageFactory
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.api.privategroup.PrivateGroupFactory
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.api.test.TestAvatarCreator
import org.briarproject.briar.desktop.GroupCountHelper
import org.briarproject.briar.desktop.attachment.media.ImageCompressor
import org.briarproject.briar.desktop.testdata.conversation.Direction
import org.briarproject.briar.desktop.testdata.conversation.Message
import org.briarproject.briar.desktop.testdata.conversation.conversations
import org.briarproject.briar.desktop.testdata.forum.Post
import org.briarproject.briar.desktop.testdata.forum.PostAuthor
import org.briarproject.briar.desktop.testdata.forum.forums
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import java.io.IOException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Random
import java.util.UUID
import java.util.concurrent.Executor
import javax.imageio.ImageIO
import javax.inject.Inject
import kotlin.math.min

@Suppress("HardCodedStringLiteral")
class DeterministicTestDataCreatorImpl @Inject internal constructor(
    private val authorFactory: AuthorFactory,
    private val clock: Clock,
    private val groupFactory: GroupFactory,
    private val groupMessageFactory: GroupMessageFactory,
    private val privateMessageFactory: PrivateMessageFactory,
    private val db: DatabaseComponent,
    private val identityManager: IdentityManager,
    private val contactManager: ContactManager,
    private val privateGroupManager: PrivateGroupManager,
    private val privateGroupFactory: PrivateGroupFactory,
    private val forumManager: ForumManager,
    private val forumFactory: ForumFactory,
    private val transportPropertyManager: TransportPropertyManager,
    private val conversationManager: ConversationManager,
    private val messagingManager: MessagingManager,
    private val blogManager: BlogManager,
    private val blogFactory: BlogFactory,
    private val blogPostFactory: BlogPostFactory,
    private val testAvatarCreator: TestAvatarCreator,
    private val avatarMessageEncoder: AvatarMessageEncoder,
    private val clientHelper: ClientHelper,
    private val imageCompressor: ImageCompressor,
    @field:IoExecutor
    @param:IoExecutor
    private val ioExecutor: Executor,
) : DeterministicTestDataCreator {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val random = Random()
    private val localAuthors: MutableMap<ContactId, LocalAuthor> = HashMap()

    override fun createTestData(
        numContacts: Int,
        numPrivateMsgs: Int,
        avatarPercent: Int,
        numPrivateGroups: Int,
        numPrivateGroupPosts: Int,
    ) {
        require(numContacts != 0 || numPrivateGroups != 0)
        require(!(avatarPercent < 0 || avatarPercent > 100))
        ioExecutor.execute {
            try {
                createTestDataOnIoExecutor(
                    numContacts,
                    numPrivateMsgs,
                    avatarPercent,
                    numPrivateGroups,
                    numPrivateGroupPosts
                )
            } catch (e: DbException) {
                LOG.w(e) { }
            }
        }
    }

    @IoExecutor
    @Throws(DbException::class)
    private fun createTestDataOnIoExecutor(
        numContacts: Int,
        numPrivateMsgs: Int,
        avatarPercent: Int,
        numPrivateGroups: Int,
        numPrivateGroupPosts: Int,
    ) {
        val contacts = createContacts(numContacts, avatarPercent)
        createPrivateMessages(contacts, numPrivateMsgs)

        val privateGroups = createPrivateGroups(contacts, numPrivateGroups)
        for (privateGroup in privateGroups) {
            createRandomPrivateGroupMessages(privateGroup, contacts, numPrivateGroupPosts)
        }

        createForums()
        createBlogPosts(contacts)
    }

    @Throws(DbException::class)
    private fun createContacts(numContacts: Int, avatarPercent: Int): List<ContactId> {
        val contacts: MutableList<ContactId> = ArrayList(numContacts)
        val localAuthor = identityManager.localAuthor

        for (i in 0 until min(numContacts, conversations.persons.size)) {
            val person = conversations.persons[i]
            val remote = authorFactory.createLocalAuthor(person.name)

            val date = person.messages.map { it.date }.maxOf { it }
            val contact = addContact(localAuthor.id, remote, null, avatarPercent, date)
            contacts.add(contact)
        }
        return contacts
    }

    @Throws(DbException::class)
    private fun addContact(
        localAuthorId: AuthorId,
        remote: LocalAuthor,
        alias: String?,
        avatarPercent: Int,
        date: LocalDateTime,
    ): ContactId {
        // prepare to add contact
        val secretKey = secretKey
        val timestamp = clock.currentTimeMillis()
        val verified = random.nextBoolean()

        // prepare transport properties
        val props = randomTransportProperties
        val contactId = db.transactionWithResult<ContactId, RuntimeException>(false) { txn: Transaction ->
            val contactId = contactManager.addContact(
                txn, remote, localAuthorId, secretKey, timestamp, true, verified, true
            )
            if (alias != null) {
                contactManager.setContactAlias(txn, contactId, alias)
            }
            transportPropertyManager.addRemoteProperties(txn, contactId, props)
            val timestamp = date.toEpochSecond(ZoneOffset.UTC) * 1000
            GroupCountHelper.resetGroupTimestamp(txn, contactId, messagingManager, clientHelper, timestamp)
            contactId
        }
        if (random.nextInt(100) + 1 <= avatarPercent) addAvatar(contactId)
        LOG.i { "Added contact ${remote.name} with transport properties: $props" }
        localAuthors[contactId] = remote
        return contactId
    }

    private val secretKey: SecretKey
        get() {
            val b = ByteArray(SecretKey.LENGTH)
            random.nextBytes(b)
            return SecretKey(b)
        }

    // Bluetooth
    private val randomTransportProperties: Map<TransportId, TransportProperties>
        get() {
            val props: MutableMap<TransportId, TransportProperties> = HashMap()
            // Bluetooth
            val bt = TransportProperties()
            val btAddress = randomBluetoothAddress
            val uuid = randomUUID
            bt[BluetoothConstants.PROP_ADDRESS] = btAddress
            bt[BluetoothConstants.PROP_UUID] = uuid
            props[BluetoothConstants.ID] = bt

            // LAN
            val lan = TransportProperties()
            val sb = StringBuilder()
            for (i in 0..3) {
                if (sb.isNotEmpty()) sb.append(',')
                sb.append(randomLanAddress)
            }
            lan[LanTcpConstants.PROP_IP_PORTS] = sb.toString()
            val port = randomPortNumber.toString()
            lan[LanTcpConstants.PROP_PORT] = port
            props[LanTcpConstants.ID] = lan

            // Tor
            val tor = TransportProperties()
            val torAddress = randomTorAddress
            tor[TorConstants.PROP_ONION_V3] = torAddress
            props[TorConstants.ID] = tor
            return props
        }

    private val randomBluetoothAddress: String
        get() {
            val mac = ByteArray(6)
            random.nextBytes(mac)
            val sb = StringBuilder(18)
            for (b in mac) {
                if (sb.isNotEmpty()) sb.append(":")
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        }

    private val randomUUID: String
        get() {
            val uuid = ByteArray(BluetoothConstants.UUID_BYTES)
            random.nextBytes(uuid)
            return UUID.nameUUIDFromBytes(uuid).toString()
        }

    // address
    // port
    private val randomLanAddress: String
        get() {
            val sb = StringBuilder()
            // address
            if (random.nextInt(5) == 0) {
                sb.append("10.")
                sb.append(random.nextInt(2)).append('.')
            } else {
                sb.append("192.168.")
            }
            sb.append(random.nextInt(2)).append('.')
            sb.append(random.nextInt(255))
            // port
            sb.append(':').append(randomPortNumber)
            return sb.toString()
        }

    private val randomPortNumber: Int
        get() = 32768 + random.nextInt(32768)

    // address
    private val randomTorAddress: String
        get() {
            val sb = StringBuilder()
            // address
            for (i in 0..15) {
                if (random.nextBoolean()) sb.append(2 + random.nextInt(6)) else sb.append((random.nextInt(26) + 'a'.code).toChar())
            }
            return sb.toString()
        }

    @Throws(DbException::class)
    private fun addAvatar(contactId: ContactId) {
        val c = contactManager.getContact(contactId)
        val authorId = c.author.id
        val groupId = groupFactory.createGroup(
            AvatarManager.CLIENT_ID,
            AvatarManager.MAJOR_VERSION, authorId.bytes
        ).id
        val `is`: InputStream = try {
            testAvatarCreator.avatarInputStream
        } catch (e: IOException) {
            LOG.w(e) {}
            return
        } ?: return
        val m = try {
            avatarMessageEncoder.encodeUpdateMessage(groupId, 0, "image/jpeg", `is`).first
        } catch (e: IOException) {
            throw DbException(e)
        }
        db.transaction<RuntimeException>(false) { txn: Transaction ->
            // TODO: Do this properly via clients without breaking encapsulation
            db.setGroupVisibility(txn, contactId, groupId, Group.Visibility.SHARED)
            db.receiveMessage(txn, contactId, m)
        }
    }

    // TODO: Do this properly via clients without breaking encapsulation
    @Throws(DbException::class)
    private fun shareGroup(contactId: ContactId, groupId: GroupId) {
        db.transaction<RuntimeException>(false) { txn: Transaction ->
            db.setGroupVisibility(txn, contactId, groupId, Group.Visibility.SHARED)
        }
    }

    @Throws(DbException::class)
    private fun createPrivateMessages(
        contacts: List<ContactId>,
        numPrivateMsgs: Int,
    ) {
        for (i in contacts.indices) {
            val contactId = contacts[i]
            // this cannot cause an IndexOutOfBoundsException here with conversation.persons
            // because we already made sure to only create as many contacts as we have
            // conversation templates available.
            val person = conversations.persons[i]
            val groupId = messagingManager.getConversationId(contactId)
            shareGroup(contactId, groupId)
            for (k in 0 until min(numPrivateMsgs, person.messages.size)) {
                createPrivateMessage(contactId, groupId, person.messages[k])
            }
        }
        LOG.i { "Created $numPrivateMsgs private messages per contact." }
    }

    @Throws(DbException::class)
    private fun createPrivateMessage(
        contactId: ContactId,
        groupId: GroupId,
        message: Message,
    ) {
        val timestamp = message.date.toEpochSecond(ZoneOffset.UTC) * 1000
        val text = message.text
        val local = message.direction == Direction.OUTGOING
        val autoDelete = random.nextBoolean()
        val images = message.images
        createPrivateMessage(contactId, groupId, text, images, timestamp, local, autoDelete)
    }

    @Throws(DbException::class)
    private fun createPrivateMessage(
        contactId: ContactId,
        groupId: GroupId,
        text: String?,
        images: List<String>,
        timestamp: Long,
        local: Boolean,
        autoDelete: Boolean,
    ) {
        val timer =
            if (autoDelete) AutoDeleteConstants.MIN_AUTO_DELETE_TIMER_MS else AutoDeleteConstants.NO_AUTO_DELETE_TIMER
        try {
            val headers = buildList {
                for (image in images.map { image(it) }) {
                    messagingManager.addLocalAttachment(
                        groupId, timestamp, "image/jpeg", image
                    ).also { add(it) }
                }
            }
            val m = privateMessageFactory.createPrivateMessage(
                groupId, timestamp, text, headers, timer
            )
            if (local) {
                messagingManager.addLocalMessage(m)
            } else {
                db.transaction<RuntimeException>(false) { txn ->
                    db.receiveMessage(txn, contactId, m.message)
                }
            }
        } catch (e: FormatException) {
            throw AssertionError(e)
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun image(imageResource: String): InputStream {
        val input = ResourceLoader.Default.load(imageResource)
        val image = input.use {
            ImageIO.read(input)
        }
        return imageCompressor.compressImage(image)
    }

    @Throws(DbException::class)
    private fun createPrivateGroups(contacts: List<ContactId>, numPrivateGroups: Int): List<PrivateGroup> {
        val privateGroups: MutableList<PrivateGroup> = ArrayList(numPrivateGroups)
        for (i in 0 until min(numPrivateGroups, GROUP_NAMES.size)) {
            // create private group
            val name = GROUP_NAMES[i]
            val creator = identityManager.localAuthor
            val privateGroup = privateGroupFactory.createPrivateGroup(name, creator)
            val joinMsg = groupMessageFactory.createJoinMessage(
                privateGroup.id,
                clock.currentTimeMillis() - i * 60 * 1000, creator
            )
            privateGroupManager.addPrivateGroup(privateGroup, joinMsg, true)

            // share with all contacts
            for (contactId in contacts) {
                shareGroup(contactId, privateGroup.id)
            }
            privateGroups.add(privateGroup)
        }
        LOG.i { "Created ${min(numPrivateGroups, GROUP_NAMES.size)} private groups." }
        return privateGroups
    }

    @Throws(DbException::class)
    private fun createRandomPrivateGroupMessages(
        privateGroup: PrivateGroup,
        contacts: List<ContactId>,
        numPrivateGroupMessages: Int,
    ) {
        // TODO
    }

    private fun createForums() {
        for (f in forums.forums) {
            // create forum
            val forum = forumManager.addForum(f.name)

            val members = f.members.associateWith {
                if (it is PostAuthor.RemoteAuthor) authorFactory.createLocalAuthor(it.name)
                else identityManager.localAuthor
            }
            // todo: create real contact to also share forum!

            // add posts
            fun addPost(post: Post, parentId: MessageId?) {
                val m = forumManager.createLocalPost(
                    forum.id,
                    post.text,
                    post.date.toEpochSecond(ZoneOffset.UTC) * 1000,
                    parentId,
                    members[post.author]!!
                )
                // todo: add non-local posts using incoming message via some contact this forum is shared with
                forumManager.addLocalPost(m)
                post.replies.forEach { addPost(it, m.message.id) }
            }
            f.posts.forEach { addPost(it, null) }
        }
        LOG.i { "Created ${forums.forums.size} forums." }
    }

    private fun createBlogPosts(contactIds: List<ContactId>) {
        val postHtmlBriar = """
            <h1>A blog post with HTML</h1>
            <p>This is a post written in HTML</p>
            <p>It contains <a href="https://briarproject.org">a link to the Briar website</a>.</p>
            <ul>
              <li> Clicking it should open a dialog,
              <li> that dialog should warn you about opening links,
              <li> and offer to open it using your default browser.
            </ul>
        """.trimIndent()

        val postHtmlDuckDuckGo = """
            <h1>Yet another blog post</h1>
            <p>This is a post written in HTML</p>
            <p>It contains <a href="https://duckduckgo.com">a link to DuckDuckGo</a>.</p>
            <ul>
              <li> Clicking it should open a dialog,
              <li> that dialog should warn you about opening links,
              <li> and offer to open it using your default browser.
            </ul>
        """.trimIndent()

        // Add blog posts for contacts
        contactIds.forEachIndexed { index, contactId ->
            // add one blog per contact
            val author = localAuthors[contactId] ?: return@forEachIndexed
            val blog = blogFactory.createBlog(author)
            blogManager.addBlog(blog)

            val post = blogPostFactory.createBlogPost(
                blog.id,
                clock.currentTimeMillis() - 500_000,
                null,
                author,
                if (index % 2 == 0) postHtmlBriar else postHtmlDuckDuckGo
            )
            blogManager.addLocalPost(post)
        }

        // Add blog post on personal blog
        val author = identityManager.localAuthor
        val blog = blogManager.getPersonalBlog(author)
        blogManager.addBlog(blog)

        val post = blogPostFactory.createBlogPost(
            blog.id,
            clock.currentTimeMillis() - 500_000,
            null,
            author,
            postHtmlBriar
        )
        blogManager.addLocalPost(post)
    }
}
