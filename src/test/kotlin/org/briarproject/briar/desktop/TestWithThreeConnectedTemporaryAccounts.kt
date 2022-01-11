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
