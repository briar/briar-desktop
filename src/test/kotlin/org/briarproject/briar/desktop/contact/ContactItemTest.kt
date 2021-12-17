package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.UniqueId
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.crypto.CryptoConstants
import org.briarproject.bramble.api.crypto.SignaturePublicKey
import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

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
            isConnected = false,
            groupCount = MessageTracker.GroupCount(0, 0, System.currentTimeMillis()),
            avatar = null
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
