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

package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.UniqueId
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.crypto.CryptoConstants
import org.briarproject.bramble.api.crypto.SignaturePublicKey
import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.identity.AuthorInfo
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

@Suppress("HardCodedStringLiteral")
class ContactItemTest {

    @Test
    fun test() {
        val random = Random(1)

        val localAuthorId = AuthorId(random.nextBytes(UniqueId.LENGTH))

        val id = AuthorId(random.nextBytes(UniqueId.LENGTH))
        val name = "Alice"
        val publicKey = SignaturePublicKey(random.nextBytes(CryptoConstants.MAX_SIGNATURE_PUBLIC_KEY_BYTES))
        val author = Author(id, Author.FORMAT_VERSION, name, publicKey)

        val contact = Contact(
            ContactId(random.nextInt()),
            author,
            localAuthorId,
            null,
            null,
            false,
        )
        val item = ContactItem(
            contact = contact,
            authorInfo = AuthorInfo(AuthorInfo.Status.UNKNOWN),
            isConnected = false,
            groupCount = MessageTracker.GroupCount(0, 0, System.currentTimeMillis()),
        )
        assertEquals("Alice", item.displayName)

        val updated = item.updateAlias("liz")
        assertEquals("liz (Alice)", updated.displayName)

        // old item did not get updated
        assertEquals("Alice", item.displayName)

        val reset = updated.updateAlias(null)
        assertEquals("Alice", reset.displayName)
    }
}
