package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.contact.PendingContact

data class PendingContactItem(
    override val idWrapper: PendingContactIdWrapper,
    val name: String,
    val alias: String?,
    override val isConnected: Boolean,
    override val isEmpty: Boolean,
    override val unread: Int,
    override val timestamp: Long
) : ContactItem {

    override val displayName = if (alias == null) name else "$alias ($name)"

    constructor(contact: PendingContact) : this(
        idWrapper = PendingContactIdWrapper(contact.id),
        name = contact.alias,
        alias = contact.alias,
        isConnected = false,
        isEmpty = true,
        unread = 0,
        timestamp = contact.timestamp
    )

    fun updateAlias(a: String?): PendingContactItem {
        return copy(alias = a)
    }
}
