package org.briarproject.briar.desktop.privategroups

import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.privategroup.GroupMessageHeader
import org.briarproject.briar.api.privategroup.PrivateGroup
import kotlin.math.max

data class PrivateGroupItem(
    val privateGroup: PrivateGroup,
    val isEmpty: Boolean,
    val unread: Int,
    val timestamp: Long
) {

    constructor(privateGroup: PrivateGroup, groupCount: MessageTracker.GroupCount) :
        this(
            privateGroup,
            isEmpty = groupCount.msgCount == 0,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )

    fun updateFromMessageHeader(h: GroupMessageHeader): PrivateGroupItem {
        return copy(
            isEmpty = false,
            unread = if (h.isRead) unread else unread + 1,
            timestamp = max(h.timestamp, timestamp)
        )
    }

    fun updateName(name: String): PrivateGroupItem {
        return copy(privateGroup = privateGroup.updateName(name))
    }

    private fun PrivateGroup.updateName(name: String): PrivateGroup {
        return PrivateGroup(group, name, creator, salt)
    }
}
