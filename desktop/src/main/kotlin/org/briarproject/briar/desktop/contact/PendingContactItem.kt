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

import org.briarproject.bramble.api.contact.PendingContact
import org.briarproject.bramble.api.contact.PendingContactState

data class PendingContactItem(
    override val idWrapper: PendingContactIdWrapper,
    val alias: String,
    override val timestamp: Long,
    val state: PendingContactState,
) : BaseContactItem {

    override val displayName = alias

    constructor(contact: PendingContact, state: PendingContactState) : this(
        idWrapper = PendingContactIdWrapper(contact.id),
        alias = contact.alias,
        timestamp = contact.timestamp,
        state = state,
    )
}
