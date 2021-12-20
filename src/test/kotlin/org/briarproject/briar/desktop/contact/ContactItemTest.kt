package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.UniqueId
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class ContactItemTest {

    @Test
    fun test() {
        val random = Random(1)
        val item = ContactItem(
            idWrapper = RealContactIdWrapper(ContactId(random.nextInt())),
            authorId = AuthorId(random.nextBytes(UniqueId.LENGTH)),
            name = "Alice",
            alias = null,
            isConnected = false,
            isEmpty = false,
            unread = 10,
            timestamp = random.nextLong()
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
