package org.briarproject.briar.desktop

import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.client.ClientHelper
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.data.BdfDictionary
import org.briarproject.bramble.api.data.BdfEntry
import org.briarproject.bramble.api.db.DbException
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.client.MessageTracker.GroupCount
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.client.MessageTrackerConstants

object GroupCountHelper {

    internal fun resetGroupTimestamp(
        txn: Transaction,
        contactId: ContactId,
        messagingManager: MessagingManager,
        clientHelper: ClientHelper,
        timestamp: Long,
    ) {
        val gc = messagingManager.getGroupCount(txn, contactId)
        val groupId = messagingManager.getConversationId(txn, contactId)
        val copy = GroupCount(gc.msgCount, gc.unreadCount, timestamp)
        storeGroupCount(clientHelper, txn, groupId, copy)
    }

    @Throws(DbException::class)
    private fun storeGroupCount(clientHelper: ClientHelper, txn: Transaction, g: GroupId, c: GroupCount) {
        try {
            val d = BdfDictionary.of(
                BdfEntry(MessageTrackerConstants.GROUP_KEY_MSG_COUNT, c.msgCount),
                BdfEntry(MessageTrackerConstants.GROUP_KEY_UNREAD_COUNT, c.unreadCount),
                BdfEntry(MessageTrackerConstants.GROUP_KEY_LATEST_MSG, c.latestMsgTime)
            )
            clientHelper.mergeGroupMetadata(txn, g, d)
        } catch (e: FormatException) {
            throw DbException(e)
        }
    }
}
