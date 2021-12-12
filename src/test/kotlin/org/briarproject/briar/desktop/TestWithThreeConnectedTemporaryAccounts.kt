package org.briarproject.briar.desktop

import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.briar.desktop.TestUtils.connectApps

fun main() = RunWithMultipleTemporaryAccounts(listOf("alice", "bob", "eve")) {
    val alice = this[0]
    val bob = this[1]
    val eve = this[2]

    listOf(bob, eve).forEach {
        it.getDeterministicTestDataCreator().createTestData(1, 2, 0, 0, 0)
        connectApps(alice, it)
    }

    // alice introduces eve to bob
    alice.run {
        var eve: Contact? = null
        var bob: Contact? = null
        getEventBus().addListener {
            when (it) {
                is ContactAddedEvent -> {
                    val contact = getContactManager().getContact(it.contactId)
                    when (contact.author.name) {
                        "eve" -> eve = contact
                        "bob" -> bob = contact
                    }
                    if (eve != null && bob != null) {
                        getIntroductionManager().makeIntroduction(eve!!, bob!!, "Eve and Bob")
                    }
                }
            }
        }
    }
}.run()
