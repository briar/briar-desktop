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

package org.briarproject.briar.desktop.privategroup.sharing

import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.privategroup.GroupMember
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName

data class GroupMemberItem(
    val authorId: AuthorId,
    val authorInfo: AuthorInfo,
    private val name: String,
    val isCreator: Boolean,
    val contactId: ContactId?,
    val isConnected: Boolean?,
) {
    val displayName = getContactDisplayName(name, authorInfo.alias)
    val isVisibleContact = contactId != null

    constructor(
        groupMember: GroupMember,
        isConnected: Boolean?,
    ) : this(
        authorId = groupMember.author.id,
        authorInfo = groupMember.authorInfo,
        name = groupMember.author.name,
        isCreator = groupMember.isCreator,
        contactId = groupMember.contactId,
        isConnected = isConnected
    )

    fun updateIsConnected(c: Boolean) =
        copy(isConnected = c)

    fun updateAuthorInfo(authorInfo: AuthorInfo) =
        copy(authorInfo = authorInfo)
}

fun loadGroupMemberItem(groupMember: GroupMember, connectionRegistry: ConnectionRegistry) =
    GroupMemberItem(
        groupMember,
        groupMember.contactId?.let { connectionRegistry.isConnected(it) }
    )
