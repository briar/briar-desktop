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

import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.plugin.LanTcpConstants

/**
 * Launches Briar Desktop with UI set up for taking a screenshot for the README file
 * or the website.
 * This includes faking connections to two contacts to make them appear as online.
 */
fun main() = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
    getContactManager().addPendingContact("briar://aatkjq4seoualafpwh4cfckdzr4vpr4slk3bbvpxklf7y7lv4ajw6", "Faythe")

    getEventBus().addListener { e ->
        if (e is ContactAddedEvent) {
            if (getContactManager().getContact(e.contactId).author.name in listOf("Bob", "Chuck")) // NON-NLS
                getIoExecutor().execute {
                    getConnectionRegistry().registerIncomingConnection(e.contactId, LanTcpConstants.ID) {}
                }
        }
    }
}.run()
