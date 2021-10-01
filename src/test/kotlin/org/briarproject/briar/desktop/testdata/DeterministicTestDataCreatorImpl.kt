package org.briarproject.briar.desktop.testdata

import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.Contact
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
import org.briarproject.bramble.api.sync.Message
import org.briarproject.bramble.api.system.Clock
import org.briarproject.bramble.util.LogUtils
import org.briarproject.briar.api.autodelete.AutoDeleteConstants
import org.briarproject.briar.api.avatar.AvatarManager
import org.briarproject.briar.api.avatar.AvatarMessageEncoder
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.test.TestAvatarCreator
import java.io.IOException
import java.io.InputStream
import java.time.ZoneOffset
import java.util.Random
import java.util.UUID
import java.util.concurrent.Executor
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.math.min

class DeterministicTestDataCreatorImpl @Inject internal constructor(
    private val authorFactory: AuthorFactory,
    private val clock: Clock,
    private val groupFactory: GroupFactory,
    private val privateMessageFactory: PrivateMessageFactory,
    private val db: DatabaseComponent,
    private val identityManager: IdentityManager,
    private val contactManager: ContactManager,
    private val transportPropertyManager: TransportPropertyManager,
    private val messagingManager: MessagingManager,
    private val testAvatarCreator: TestAvatarCreator,
    private val avatarMessageEncoder: AvatarMessageEncoder,
    @field:IoExecutor @param:IoExecutor private val ioExecutor: Executor
) : DeterministicTestDataCreator {

    companion object {
        private val LOG = Logger.getLogger(DeterministicTestDataCreatorImpl::class.java.name)
    }

    private val random = Random()
    private val localAuthors: MutableMap<Contact, LocalAuthor> = HashMap()
    override fun createTestData(
        numContacts: Int,
        numPrivateMsgs: Int,
        avatarPercent: Int
    ) {
        require(numContacts != 0)
        require(!(avatarPercent < 0 || avatarPercent > 100))
        ioExecutor.execute {
            try {
                createTestDataOnIoExecutor(
                    min(numContacts, conversations.persons.size), numPrivateMsgs,
                    avatarPercent
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
        avatarPercent: Int
    ) {
        val contacts = createContacts(numContacts, avatarPercent)
        createPrivateMessages(contacts, numPrivateMsgs)
    }

    @Throws(DbException::class)
    private fun createContacts(numContacts: Int, avatarPercent: Int): List<Contact> {
        val contacts: MutableList<Contact> = ArrayList(numContacts)
        val localAuthor = identityManager.localAuthor

        for (i in 0 until numContacts) {
            val person = conversations.persons[i]
            val remote = authorFactory.createLocalAuthor(person.name)
            val contact = addContact(localAuthor.id, remote, null, avatarPercent)
            contacts.add(contact)
        }
        return contacts
    }

    @Throws(DbException::class)
    private fun addContact(
        localAuthorId: AuthorId,
        remote: LocalAuthor,
        alias: String?,
        avatarPercent: Int
    ): Contact {
        // prepare to add contact
        val secretKey = secretKey
        val timestamp = clock.currentTimeMillis()
        val verified = random.nextBoolean()

        // prepare transport properties
        val props = randomTransportProperties
        val contact = db.transactionWithResult<Contact, RuntimeException>(false) { txn: Transaction ->
            val contactId = contactManager.addContact(
                txn, remote, localAuthorId, secretKey, timestamp, true, verified, true
            )
            if (alias != null) {
                contactManager.setContactAlias(txn, contactId, alias)
            }
            transportPropertyManager.addRemoteProperties(txn, contactId, props)
            db.getContact(txn, contactId)
        }
        if (random.nextInt(100) + 1 <= avatarPercent) addAvatar(contact)
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Added contact ${remote.name} with transport properties: $props")
        }
        localAuthors[contact] = remote
        return contact
    }

    @Throws(DbException::class)
    override fun addContact(name: String, alias: String?, avatar: Boolean): Contact {
        val localAuthor = identityManager.localAuthor
        val remote = authorFactory.createLocalAuthor(name)
        val avatarPercent = if (avatar) 100 else 0
        return addContact(localAuthor.id, remote, alias, avatarPercent)
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
            tor[TorConstants.PROP_ONION_V2] = torAddress
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
    private fun addAvatar(c: Contact) {
        val authorId = c.author.id
        val groupId = groupFactory.createGroup(
            AvatarManager.CLIENT_ID,
            AvatarManager.MAJOR_VERSION, authorId.bytes
        ).id
        val `is`: InputStream = try {
            testAvatarCreator.avatarInputStream
        } catch (e: IOException) {
            LogUtils.logException(LOG, Level.WARNING, e)
            return
        } ?: return
        val m: Message = try {
            avatarMessageEncoder.encodeUpdateMessage(groupId, 0, "image/jpeg", `is`).first
        } catch (e: IOException) {
            throw DbException(e)
        }
        db.transaction<RuntimeException>(false) { txn: Transaction ->
            // TODO: Do this properly via clients without breaking encapsulation
            db.setGroupVisibility(txn, c.id, groupId, Group.Visibility.SHARED)
            db.receiveMessage(txn, c.id, m)
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
        contacts: List<Contact>,
        numPrivateMsgs: Int
    ) {
        for (i in contacts.indices) {
            val contact = contacts[i]
            val person = conversations.persons[i]
            val group = messagingManager.getContactGroup(contact)
            shareGroup(contact.id, group.id)
            for (k in 0 until min(numPrivateMsgs, person.messages.size)) {
                createPrivateMessage(contact.id, group.id, person.messages[k])
            }
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Created $numPrivateMsgs private messages per contact.")
        }
    }

    @Throws(DbException::class)
    private fun createPrivateMessage(
        contactId: ContactId,
        groupId: GroupId,
        message: org.briarproject.briar.desktop.testdata.Message
    ) {
        val timestamp = message.date.toEpochSecond(ZoneOffset.UTC)
        val text = message.text
        val local = message.direction == Direction.OUTGOING
        val autoDelete = random.nextBoolean()
        createPrivateMessage(contactId, groupId, text, timestamp, local, autoDelete)
    }

    @Throws(DbException::class)
    private fun createPrivateMessage(
        contactId: ContactId,
        groupId: GroupId,
        text: String,
        timestamp: Long,
        local: Boolean,
        autoDelete: Boolean
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
                db.transaction<RuntimeException>(false) { txn: Transaction ->
                    db.receiveMessage(txn, contactId, m.message)
                }
            }
        } catch (e: FormatException) {
            throw AssertionError(e)
        }
    }
}
