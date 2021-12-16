package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.contact.PendingContact

data class PendingContactItem(
    override val idWrapper: PendingContactIdWrapper,
    val alias: String,
    override val timestamp: Long
) : BaseContactItem {

    override val displayName = alias

    constructor(contact: PendingContact) : this(
        idWrapper = PendingContactIdWrapper(contact.id),
        alias = contact.alias,
        timestamp = contact.timestamp
    )
}
