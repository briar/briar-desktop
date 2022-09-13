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

import kotlinx.coroutines.delay
import org.briarproject.bramble.api.plugin.LanTcpConstants

/**
 * Launches Briar Desktop with UI set up for taking a screenshot for the README file.
 * This includes faking connections to two contacts to make them appear as online.
 */
fun main() = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
    getContactManager().addPendingContact("briar://aatkjq4seoualafpwh4cfckdzr4vpr4slk3bbvpxklf7y7lv4ajw6", "Faythe")
    // Need to wait a moment before registering incoming connections
    delay(1000)
    getIoExecutor().execute {
        val contacts = getContactManager().contacts
        contacts.forEach { contact ->
            if (contact.author.name == "Bob" || contact.author.name == "Chuck") // NON-NLS
                getConnectionRegistry().registerIncomingConnection(contact.id, LanTcpConstants.ID) {}
        }
    }
}.run()
