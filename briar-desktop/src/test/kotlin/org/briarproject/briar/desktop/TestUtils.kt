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

@file:Suppress("HardCodedStringLiteral")

package org.briarproject.briar.desktop

import io.github.oshai.kotlinlogging.KotlinLogging
import org.briarproject.bramble.api.Bytes.compare
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.PendingContactId
import org.briarproject.bramble.api.crypto.SecretKey
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.bramble.api.plugin.TransportId
import org.briarproject.bramble.api.properties.TransportProperties
import org.briarproject.bramble.api.versioning.event.ClientVersionUpdatedEvent
import org.briarproject.briar.api.autodelete.AutoDeleteConstants.NO_AUTO_DELETE_TIMER
import org.briarproject.briar.api.forum.ForumManager
import org.briarproject.briar.api.forum.event.ForumInvitationRequestReceivedEvent
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.api.privategroup.event.GroupInvitationRequestReceivedEvent
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.SHAREABLE
import org.briarproject.briar.desktop.utils.FileUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.security.GeneralSecurityException
import kotlin.io.path.absolute

object TestUtils {

    private val LOG = KotlinLogging.logger {}

    fun getDataDir(): Path {
        val dataDir = Files.createTempDirectory("briar")
        if (!Files.exists(dataDir)) {
            throw IOException("Could not create directory: ${dataDir.absolute()}")
        } else if (!Files.isDirectory(dataDir)) {
            throw IOException("Data dir is not a directory: ${dataDir.absolute()}")
        }
        FileUtils.setRWX(dataDir)
        return dataDir
    }

    internal fun List<BriarDesktopTestApp>.connectAllPending() {
        forEachIndexed { i, app1 ->
            forEachIndexed inner@{ k, app2 ->
                if (i >= k) return@inner
                connectAppsPending(app1, app2)
            }
        }
    }

    internal fun connectAppsPending(app1: BriarDesktopTestApp, app2: BriarDesktopTestApp) {
        val cm1 = app1.getContactManager()
        val cm2 = app2.getContactManager()
        val name1 = app1.getIdentityManager().localAuthor.name
        val name2 = app2.getIdentityManager().localAuthor.name
        cm1.addPendingContact(cm2.handshakeLink, name2)
        cm2.addPendingContact(cm1.handshakeLink, name1)
    }

    internal fun List<BriarDesktopTestApp>.connectAllInstantly() {
        forEachIndexed { i, app1 ->
            forEachIndexed inner@{ k, app2 ->
                if (i >= k) return@inner
                connectAppsInstantly(app1, app2)
            }
        }
    }

    internal fun connectAppsInstantly(app1: BriarDesktopTestApp, app2: BriarDesktopTestApp) {
        val cm1 = app1.getContactManager()
        val cm2 = app2.getContactManager()
        val im1 = app1.getIdentityManager()
        val im2 = app2.getIdentityManager()
        val name1 = im1.localAuthor.name
        val name2 = im2.localAuthor.name
        val pc1 = cm1.addPendingContact(cm2.handshakeLink, name2)
        val pc2 = cm2.addPendingContact(cm1.handshakeLink, name1)

        val masterKey = app1.getCryptoComponent().generateSecretKey()
        // As in TransportCryptoImpl#isAlice()
        val firstAlice = compare(im1.localAuthor.publicKey.encoded, im2.localAuthor.publicKey.encoded) < 0
        exchange(app1, app2, pc1.id, masterKey, firstAlice)
        exchange(app2, app1, pc2.id, masterKey, !firstAlice)
    }

    // Derived from ContactExchangeManagerImpl
    private fun exchange(
        app1: BriarDesktopTestApp,
        app2: BriarDesktopTestApp,
        p: PendingContactId,
        masterKey: SecretKey,
        alice: Boolean,
    ) {
        // Get the local author and transport properties
        val localAuthor = app1.getIdentityManager().localAuthor
        val remoteAuthor = app2.getIdentityManager().localAuthor
        val remoteProperties = app2.getTransportPropertyManager().localProperties

        val timestamp = System.currentTimeMillis()

        // Add the contact
        addContact(
            app1, p, remoteAuthor, localAuthor,
            masterKey, timestamp, alice, remoteProperties
        )

        // Contact exchange succeeded
        LOG.info { "Contact exchange succeeded" }
    }

    private fun addContact(
        app: BriarDesktopTestApp,
        pendingContactId: PendingContactId,
        remoteAuthor: Author,
        localAuthor: LocalAuthor,
        masterKey: SecretKey,
        timestamp: Long,
        alice: Boolean,
        remoteProperties: Map<TransportId, TransportProperties>,
    ) {
        app.getBriarExecutors().onDbThreadWithTransaction(false) { txn ->
            try {
                val contactId = app.getContactManager().addContact(
                    txn, pendingContactId,
                    remoteAuthor, localAuthor.id, masterKey,
                    timestamp, alice, true, true
                )
                app.getTransportPropertyManager().addRemoteProperties(
                    txn, contactId,
                    remoteProperties
                )
            } catch (e: GeneralSecurityException) {
                // Pending contact's public key is invalid
                throw FormatException()
            }
        }
    }

    internal fun List<BriarDesktopTestApp>.createForumForAll() {
        if (isEmpty()) return

        val creator = get(0)
        val contacts = drop(1)

        // create forum
        val forum = creator.getForumManager().addForum("Shared Forum")

        // invite all contacts
        creator.getEventBus().addListener { e ->
            if (e is ClientVersionUpdatedEvent && e.clientVersion.clientId == ForumManager.CLIENT_ID) {
                creator.getBriarExecutors().onDbThread {
                    val contact = creator.getContactManager().getContact(e.contactId)
                    val sharingManager = creator.getForumSharingManager()
                    if (sharingManager.getSharingStatus(forum.id, contact) == SHAREABLE)
                        sharingManager.sendInvitation(forum.id, e.contactId, null)
                }
            }
        }

        // accept invitation at all contacts
        contacts.forEach { app ->
            app.getEventBus().addListenerOnce { e ->
                if (e is ForumInvitationRequestReceivedEvent) {
                    val contact = app.getContactManager().getContact(e.contactId)
                    app.getForumSharingManager().respondToInvitation(forum, contact, true)
                    return@addListenerOnce true
                }
                false
            }
        }
    }

    internal fun List<BriarDesktopTestApp>.createPrivateGroupForAll() {
        if (isEmpty()) return

        val creator = get(0)
        val contacts = drop(1)

        // create private group
        val author = creator.getIdentityManager().localAuthor
        val group = creator.getPrivateGroupFactory().createPrivateGroup("Shared Private Group", author)
        val joinMsg = creator.getGroupMessageFactory().createJoinMessage(
            group.id, System.currentTimeMillis(), author
        )
        creator.getPrivateGroupManager().addPrivateGroup(group, joinMsg, true)

        // invite all contacts
        creator.getEventBus().addListener { e ->
            if (e is ClientVersionUpdatedEvent && e.clientVersion.clientId == PrivateGroupManager.CLIENT_ID) {
                creator.getBriarExecutors().onDbThread {
                    val contact = creator.getContactManager().getContact(e.contactId)
                    val invitationManager = creator.getGroupInvitationManager()
                    if (invitationManager.getSharingStatus(contact, group.id) == SHAREABLE) {
                        val timestamp = System.currentTimeMillis()
                        val signature = creator.getGroupInvitationFactory().signInvitation(
                            contact, group.id, timestamp, author.privateKey
                        )
                        invitationManager.sendInvitation(
                            group.id, e.contactId, null,
                            timestamp, signature, NO_AUTO_DELETE_TIMER
                        )
                    }
                }
            }
        }

        // accept invitation at all contacts
        contacts.forEach { app ->
            app.getEventBus().addListenerOnce { e ->
                if (e is GroupInvitationRequestReceivedEvent) {
                    app.getGroupInvitationManager().respondToInvitation(e.contactId, group, true)
                    return@addListenerOnce true
                }
                false
            }
        }
    }
}

fun EventBus.addListenerOnce(listener: (Event) -> Boolean) =
    addListener(object : EventListener {
        override fun eventOccurred(e: Event) {
            if (listener(e))
                removeListener(this)
        }
    })
