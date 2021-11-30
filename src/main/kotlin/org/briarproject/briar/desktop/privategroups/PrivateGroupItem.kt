package org.briarproject.briar.desktop.privategroups

import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.privategroup.PrivateGroup

data class PrivateGroupItem(
    val privateGroup: PrivateGroup,
    val msgCount: Int,
    val unread: Int,
    val timestamp: Long
) {

    constructor(privateGroup: PrivateGroup, groupCount: MessageTracker.GroupCount) :
        this(
            privateGroup,
            msgCount = groupCount.msgCount,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )
}
