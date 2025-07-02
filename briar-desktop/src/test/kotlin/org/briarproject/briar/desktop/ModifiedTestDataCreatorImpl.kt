package org.briarproject.briar.desktop

import org.briarproject.bramble.api.FeatureFlags
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.crypto.CryptoComponent
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
import org.briarproject.bramble.api.sync.Message
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.system.Clock
import org.briarproject.bramble.util.LogUtils
import org.briarproject.bramble.util.StringUtils
import org.briarproject.bramble.util.StringUtils.toUtf8
import org.briarproject.briar.api.autodelete.AutoDeleteConstants
import org.briarproject.briar.api.avatar.AvatarManager
import org.briarproject.briar.api.avatar.AvatarMessageEncoder
import org.briarproject.briar.api.blog.BlogManager
import org.briarproject.briar.api.blog.BlogPostFactory
import org.briarproject.briar.api.forum.Forum
import org.briarproject.briar.api.forum.ForumConstants
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.api.forum.ForumPost
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.privategroup.GroupMessage
import org.briarproject.briar.api.privategroup.GroupMessageFactory
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.api.privategroup.PrivateGroupFactory
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationFactory
import org.briarproject.briar.api.test.TestAvatarCreator
import org.briarproject.briar.api.test.TestDataCreator
import org.briarproject.briar.test.TestData
import org.briarproject.nullsafety.NotNullByDefault
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.util.Random
import java.util.UUID
import java.util.concurrent.Executor
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.math.min

@NotNullByDefault
class ModifiedTestDataCreatorImpl @Inject internal constructor(
    private val authorFactory: AuthorFactory,
    private val clock: Clock,
    private val groupFactory: GroupFactory,
    private val privateMessageFactory: PrivateMessageFactory,
    private val blogPostFactory: BlogPostFactory,
    private val db: DatabaseComponent,
    private val identityManager: IdentityManager,
    private val crypto: CryptoComponent,
    private val contactManager: ContactManager,
    private val transportPropertyManager: TransportPropertyManager,
    private val messagingManager: MessagingManager,
    private val blogManager: BlogManager,
    private val forumManager: ForumManager,
    private val privateGroupManager: PrivateGroupManager,
    private val privateGroupFactory: PrivateGroupFactory,
    private val groupMessageFactory: GroupMessageFactory,
    private val groupInvitationFactory: GroupInvitationFactory,
    private val testAvatarCreator: TestAvatarCreator,
    private val avatarMessageEncoder: AvatarMessageEncoder,
    private val featureFlags: FeatureFlags,
    @field:IoExecutor @param:IoExecutor private val ioExecutor: Executor,
) : TestDataCreator {
    private val LOG: Logger = Logger.getLogger(ModifiedTestDataCreatorImpl::class.java.name)

    private val random = Random()
    private val localAuthors: MutableMap<Contact, LocalAuthor> = HashMap()

    override fun createTestData(
        numContacts: Int,
        numPrivateMsgs: Int,
        avatarPercent: Int,
        numBlogPosts: Int,
        numForums: Int,
        numForumPosts: Int,
        numPrivateGroups: Int,
        numPrivateGroupMessages: Int,
    ) {
        require(numContacts != 0)
        require(!(avatarPercent < 0 || avatarPercent > 100))
        ioExecutor.execute {
            try {
                createTestDataOnIoExecutor(
                    numContacts, numPrivateMsgs,
                    avatarPercent, numBlogPosts, numForums, numForumPosts,
                    numPrivateGroups, numPrivateGroupMessages
                )
            } catch (e: DbException) {
                LogUtils.logException(LOG, Level.WARNING, e)
            }
        }
    }

    @IoExecutor
    @Throws(DbException::class)
    private fun createTestDataOnIoExecutor(
        numContacts: Int,
        numPrivateMsgs: Int,
        avatarPercent: Int,
        numBlogPosts: Int,
        numForums: Int,
        numForumPosts: Int,
        numPrivateGroups: Int,
        numPrivateGroupMessages: Int,
    ) {
        val contacts = createContacts(numContacts, avatarPercent)
        createPrivateMessages(contacts, numPrivateMsgs)
        createBlogPosts(contacts, numBlogPosts)
        val forums = createForums(contacts, numForums)
        for (forum in forums) {
            createRandomForumPosts(forum, contacts, numForumPosts)
        }
        val groups =
            createPrivateGroups(contacts, numPrivateGroups)
        for (group in groups) {
            createRandomPrivateGroupMessages(
                group, contacts,
                numPrivateGroupMessages
            )
        }
    }

    @Throws(DbException::class)
    private fun createContacts(numContacts: Int, avatarPercent: Int): List<Contact> {
        val contacts: MutableList<Contact> = ArrayList(numContacts)
        val localAuthor = identityManager.localAuthor
        for (i in 0..numContacts - 1) {
            val remote = randomAuthor
            val contact = addContact(
                localAuthor.id, remote,
                random.nextBoolean(), avatarPercent
            )
            contacts.add(contact)
        }
        return contacts
    }

    @Throws(DbException::class)
    private fun addContact(
        localAuthorId: AuthorId,
        remote: LocalAuthor,
        alias: Boolean,
        avatarPercent: Int,
    ): Contact {
        // prepare to add contact
        val secretKey = secretKey
        val timestamp = clock.currentTimeMillis()
        val verified = random.nextBoolean()

        // prepare transport properties
        val props =
            randomTransportProperties

        val contact = db.transactionWithResult<Contact, RuntimeException>(
            false
        ) { txn: Transaction? ->
            val contactId = contactManager.addContact(
                txn, remote,
                localAuthorId, secretKey, timestamp, true, verified, true
            )
            if (alias) {
                contactManager.setContactAlias(
                    txn, contactId,
                    randomAuthorName
                )
            }
            transportPropertyManager.addRemoteProperties(txn, contactId, props)
            db.getContact(txn, contactId)
        }
        if (random.nextInt(100) + 1 <= avatarPercent) addAvatar(contact)

        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(
                "Added contact " + remote.name +
                    " with transport properties: " + props
            )
        }
        localAuthors[contact] = remote
        return contact
    }

    @Throws(DbException::class)
    override fun addContact(name: String, alias: Boolean, avatar: Boolean): Contact {
        val localAuthor = identityManager.localAuthor
        val remote = authorFactory.createLocalAuthor(name)
        val avatarPercent = if (avatar) 100 else 0
        return addContact(localAuthor.id, remote, alias, avatarPercent)
    }

    private val randomAuthorName: String
        get() {
            val i = random.nextInt(TestData.AUTHOR_NAMES.size)
            return TestData.AUTHOR_NAMES[i]
        }

    private val randomAuthor: LocalAuthor
        get() = authorFactory.createLocalAuthor(randomAuthorName)

    private val secretKey: SecretKey
        get() {
            val b = ByteArray(SecretKey.LENGTH)
            random.nextBytes(b)
            return SecretKey(b)
        }

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
                if (sb.length > 0) sb.append(',')
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
                if (sb.length > 0) sb.append(":")
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

    private val randomTorAddress: String
        get() {
            val pubkeyBytes =
                crypto.generateSignatureKeyPair().public.encoded
            return crypto.encodeOnion(pubkeyBytes)
        }

    @Throws(DbException::class)
    private fun addAvatar(c: Contact) {
        val authorId = c.author.id
        val groupId = groupFactory.createGroup(
            AvatarManager.CLIENT_ID,
            AvatarManager.MAJOR_VERSION, authorId.bytes
        ).id
        val `is`: InputStream?
        try {
            `is` = testAvatarCreator.avatarInputStream
        } catch (e: IOException) {
            LogUtils.logException(LOG, Level.WARNING, e)
            return
        }
        if (`is` == null) return
        val m: Message
        try {
            m = avatarMessageEncoder.encodeUpdateMessage(
                groupId, 0,
                "image/jpeg", `is`
            ).first
        } catch (e: IOException) {
            throw DbException(e)
        }
        db.transaction<RuntimeException>(false) { txn: Transaction? ->
            // TODO: Do this properly via clients without breaking encapsulation
            db.setGroupVisibility(txn, c.id, groupId, Group.Visibility.SHARED)
            db.receiveMessage(txn, c.id, m)
        }
    }

    // TODO: Do this properly via clients without breaking encapsulation
    @Throws(DbException::class)
    private fun shareGroup(contactId: ContactId, groupId: GroupId) {
        db.transaction<RuntimeException>(
            false
        ) { txn: Transaction? ->
            db.setGroupVisibility(
                txn,
                contactId,
                groupId,
                Group.Visibility.SHARED
            )
        }
    }

    @Throws(DbException::class)
    private fun createPrivateMessages(
        contacts: List<Contact>,
        numPrivateMsgs: Int,
    ) {
        for (contact in contacts) {
            val group = messagingManager.getContactGroup(contact)
            shareGroup(contact.id, group.id)
            for (i in 0..numPrivateMsgs - 1) {
                createRandomPrivateMessage(contact.id, group.id, i)
            }
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(
                "Created " + numPrivateMsgs +
                    " private messages per contact."
            )
        }
    }

    @Throws(DbException::class)
    private fun createRandomPrivateMessage(
        contactId: ContactId,
        groupId: GroupId,
        num: Int,
    ) {
        val timestamp = clock.currentTimeMillis() - num.toLong() * 60 * 1000
        val text = randomText
        val local = random.nextBoolean()
        val autoDelete = random.nextBoolean()
        createPrivateMessage(
            contactId, groupId, text, timestamp, local,
            autoDelete
        )
    }

    @Throws(DbException::class)
    private fun createPrivateMessage(
        contactId: ContactId,
        groupId: GroupId,
        text: String,
        timestamp: Long,
        local: Boolean,
        autoDelete: Boolean,
    ) {
        val timer =
            if (autoDelete) AutoDeleteConstants.MIN_AUTO_DELETE_TIMER_MS else AutoDeleteConstants.NO_AUTO_DELETE_TIMER
        try {
            val m = privateMessageFactory.createPrivateMessage(
                groupId, timestamp, text, emptyList(), timer
            )
            if (local) {
                messagingManager.addLocalMessage(m)
            } else {
                db.transaction<RuntimeException>(
                    false
                ) { txn: Transaction? -> db.receiveMessage(txn, contactId, m.message) }
            }
        } catch (e: FormatException) {
            throw AssertionError(e)
        }
    }

    @Throws(DbException::class)
    private fun createBlogPosts(contacts: List<Contact>, numBlogPosts: Int) {
        if (!featureFlags.shouldEnableBlogsInCore()) return
        val localAuthor = identityManager.localAuthor
        val ours = blogManager.getPersonalBlog(localAuthor)
        for (contact in contacts) {
            val theirs = blogManager.getPersonalBlog(contact.author)
            shareGroup(contact.id, ours.id)
            shareGroup(contact.id, theirs.id)
        }
        for (i in 0..numBlogPosts - 1) {
            val contact = contacts[random.nextInt(contacts.size)]
            val author = localAuthors[contact]
            addBlogPost(contact.id, author, i)
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Created $numBlogPosts blog posts.")
        }
    }

    @Throws(DbException::class)
    private fun addBlogPost(contactId: ContactId, author: LocalAuthor?, num: Int) {
        val blog = blogManager.getPersonalBlog(author)
        val timestamp = clock.currentTimeMillis() - num.toLong() * 60 * 1000
        val text = randomText
        try {
            val blogPost = blogPostFactory.createBlogPost(
                blog.id,
                timestamp, null, author, text
            )
            db.transaction<RuntimeException>(
                false
            ) { txn: Transaction? ->
                db.receiveMessage(
                    txn,
                    contactId,
                    blogPost.message
                )
            }
        } catch (e: FormatException) {
            throw AssertionError(e)
        } catch (e: GeneralSecurityException) {
            throw AssertionError(e)
        }
    }

    @Throws(DbException::class)
    private fun createForums(contacts: List<Contact>, numForums: Int): List<Forum> {
        if (!featureFlags.shouldEnableForumsInCore()) return emptyList()
        val forums: MutableList<Forum> = ArrayList(numForums)
        for (i in 0..numForums - 1) {
            // create forum
            val name = TestData.GROUP_NAMES[random.nextInt(TestData.GROUP_NAMES.size)]
            val forum = forumManager.addForum(name)

            // share with all contacts
            for (contact in contacts) {
                shareGroup(contact.id, forum.id)
            }
            forums.add(forum)
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Created $numForums forums.")
        }
        return forums
    }

    @Throws(DbException::class)
    private fun createRandomForumPosts(
        forum: Forum,
        contacts: List<Contact>,
        numForumPosts: Int,
    ) {
        val posts: MutableList<ForumPost> = ArrayList()
        val len = ForumConstants.MAX_FORUM_POST_TEXT_LENGTH - 10
        for (i in 0..numForumPosts - 1) {
            val contact = contacts[random.nextInt(contacts.size)]
            val author = localAuthors[contact]
            val timestamp = clock.currentTimeMillis() - i.toLong() * 60 * 1000
            val text = getRandomText(len)
            var parent: MessageId? = null
            if (random.nextBoolean() && posts.size > 0) {
                val parentPost = posts[random.nextInt(posts.size)]
                parent = parentPost.message.id
            }
            val post = forumManager.createLocalPost(
                forum.id, text,
                timestamp, parent, author
            )
            posts.add(post)
            db.transaction<RuntimeException>(
                false
            ) { txn: Transaction? -> db.receiveMessage(txn, contact.id, post.message) }
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Created $numForumPosts forum posts.")
        }
    }

    @Throws(DbException::class)
    private fun createPrivateGroups(
        contacts: List<Contact>,
        numPrivateGroups: Int,
    ): List<PrivateGroup> {
        if (!featureFlags.shouldEnablePrivateGroupsInCore()) return emptyList()
        val groups: MutableList<PrivateGroup> = ArrayList(numPrivateGroups)
        for (i in 0..numPrivateGroups - 1) {
            // create private group
            val name = TestData.GROUP_NAMES[random.nextInt(TestData.GROUP_NAMES.size)]
            val creator = identityManager.localAuthor
            val group =
                privateGroupFactory.createPrivateGroup(name, creator)
            val joinMsg = groupMessageFactory.createJoinMessage(
                group.id,
                clock.currentTimeMillis() - (100 - i).toLong() * 60 * 1000,
                creator
            )
            privateGroupManager.addPrivateGroup(group, joinMsg, true)
            groups.add(group)
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Created $numPrivateGroups private groups.")
        }
        return groups
    }

    @Throws(DbException::class)
    private fun createRandomPrivateGroupMessages(
        group: PrivateGroup,
        contacts: List<Contact>,
        amount: Int,
    ) {
        val messages: MutableList<GroupMessage> = ArrayList()
        val creatorPrivateKey =
            identityManager.localAuthor.privateKey
        var numMembers = random.nextInt(contacts.size)
        if (numMembers == 0) numMembers++
        val membersLastMessage: MutableMap<Contact, MessageId> = HashMap()
        val members: MutableList<Contact> = ArrayList(numMembers)
        for (i in 0..numMembers - 1) {
            val contact = contacts[i]
            members.add(contact)
        }
        for (i in 0..amount - 1) {
            val contact = members[random.nextInt(numMembers)]
            val author = localAuthors[contact]
            val timestamp =
                clock.currentTimeMillis() -
                    (amount - i).toLong() * 60 * 1000

            val msg: GroupMessage
            if (!membersLastMessage.containsKey(contact)) {
                // join message as first message of member
                shareGroup(contact.id, group.id)
                val inviteTimestamp = timestamp - 1
                val creatorSignature =
                    groupInvitationFactory.signInvitation(
                        contact,
                        group.id, inviteTimestamp,
                        creatorPrivateKey
                    )
                msg = groupMessageFactory.createJoinMessage(
                    group.id,
                    timestamp, author, inviteTimestamp,
                    creatorSignature
                )
            } else {
                // random text after first message
                val text = randomText
                var parent: MessageId? = null
                if (random.nextBoolean() && messages.size > 0) {
                    val parentMessage =
                        messages[random.nextInt(messages.size)]
                    parent = parentMessage.message.id
                }
                val lastMsg = membersLastMessage[contact]
                msg = groupMessageFactory.createGroupMessage(
                    group.id, timestamp, parent, author, text,
                    lastMsg
                )
                messages.add(msg)
            }
            membersLastMessage[contact] = msg.message.id
            db.transaction<RuntimeException>(
                false
            ) { txn: Transaction? -> db.receiveMessage(txn, contact.id, msg.message) }
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Created $amount private group messages.")
        }
    }

    private val randomText: String
        get() {
            val minLength = 3 + random.nextInt(500)
            val maxWordLength = 15
            val sb = StringBuilder()
            while (sb.length < minLength) {
                if (sb.length > 0) sb.append(' ')
                sb.append(StringUtils.getRandomString(random.nextInt(maxWordLength) + 1))
            }
            if (random.nextBoolean()) {
                sb.append(" \uD83D\uDC96 \uD83E\uDD84 \uD83C\uDF08")
            }
            return sb.toString()
        }

    private fun getRandomText(length: Int): String {
        val emojis = Character.toString(0x1F308) +
            Character.toString(0x1F984) +
            Character.toString(0xFDFD) +
            Character.toString(0x1242B) +
            Character.toString(0x12219) +
            Character.toString(0x2E3B) +
            Character.toString(0xA9C5) +
            Character.toString(0x102A) +
            Character.toString(0x0BF5) +
            Character.toString(0x0BF8) +
            Character.toString(0x2031)
        val emojisSize = toUtf8(emojis).size
        val maxWordLength = 15
        val sb = StringBuilder()
        while (toUtf8(sb.toString()).size < length) {
            if (sb.isNotEmpty()) sb.append(' ')
            val currentLength: Int = toUtf8(sb.toString()).size
            if (currentLength == length) {
                break
            }
            val max = min((length - currentLength).toDouble(), maxWordLength.toDouble()).toInt()
            sb.append(StringUtils.getRandomString(random.nextInt(max) + 1))
            val remaining: Int = length - toUtf8(sb.toString()).size
            if (remaining >= emojisSize && random.nextBoolean()) {
                sb.append(emojis)
            }
        }
        return sb.toString()
    }
}
