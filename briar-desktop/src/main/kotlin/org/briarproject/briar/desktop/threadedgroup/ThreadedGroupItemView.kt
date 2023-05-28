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

package org.briarproject.briar.desktop.threadedgroup

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.forum.ForumStrings
import org.briarproject.briar.desktop.ui.NumberBadge
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.appendCommaSeparated
import org.briarproject.briar.desktop.utils.buildBlankAnnotatedString
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "name" to "This is a test forum! This is a test forum! This is a test forum! This is a test forum!",
    "creator" to "Bobby",
    "isDissolved" to false,
    "msgCount" to 42,
    "unread" to 23,
    "timestamp" to Instant.now().toEpochMilli(),
) {
    val item = object : ThreadedGroupItem {
        override val id: GroupId = GroupId(getRandomIdPersistent())
        override val name: String = getStringParameter("name")
        override val creator: String = getStringParameter("creator")
        override val isDissolved: Boolean = getBooleanParameter("isDissolved")
        override val msgCount: Int = getIntParameter("msgCount")
        override val unread: Int = getIntParameter("unread")
        override val timestamp: Long = getLongParameter("timestamp")
    }
    ThreadedGroupItemView(ForumStrings, item)
}

@Composable
fun ThreadedGroupItemView(
    strings: ThreadedGroupStrings,
    threadedGroupItem: ThreadedGroupItem,
    modifier: Modifier = Modifier,
) = Row(
    horizontalArrangement = spacedBy(12.dp),
    verticalAlignment = CenterVertically,
    modifier = modifier
        // allows content to be bottom-aligned
        .height(IntrinsicSize.Min)
        .semantics {
            text = getDescription(strings, threadedGroupItem)
        }.alpha(if (threadedGroupItem.isDissolved) 0.5f else 1f),
) {
    Box(Modifier.align(Top).padding(vertical = 8.dp)) {
        ThreadedGroupCircle(threadedGroupItem)
        NumberBadge(
            num = threadedGroupItem.unread,
            modifier = Modifier.align(TopEnd).offset(6.dp, (-6).dp)
        )
    }
    ThreadedGroupItemViewInfo(strings, threadedGroupItem)
}

private fun getDescription(strings: ThreadedGroupStrings, item: ThreadedGroupItem) = buildBlankAnnotatedString {
    append(item.name)
    item.creator?.let { creator -> appendCommaSeparated(i18nF("group.card.created", creator)) }
    if (item.unread > 0) appendCommaSeparated(strings.unreadCount(item.unread))
    appendCommaSeparated(strings.messageCount(item.msgCount))
    if (item.msgCount > 0) appendCommaSeparated(strings.lastMessage(getFormattedTimestamp(item.timestamp)))
}

@Composable
private fun ThreadedGroupItemViewInfo(strings: ThreadedGroupStrings, threadedGroupItem: ThreadedGroupItem) = Column(
    horizontalAlignment = Start,
) {
    Spacer(Modifier.weight(1f, fill = true))
    Text(
        text = threadedGroupItem.name,
        style = MaterialTheme.typography.body1,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
    Spacer(Modifier.heightIn(min = 4.dp).weight(1f, fill = true))
    threadedGroupItem.creator?.let { creator ->
        Text(
            text = i18nF("group.card.created", creator),
            style = MaterialTheme.typography.caption,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp),
        )
    }
    Row(
        horizontalArrangement = SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!threadedGroupItem.isDissolved) {
            Text(
                text = strings.messageCount(threadedGroupItem.msgCount),
                style = MaterialTheme.typography.caption
            )
            if (threadedGroupItem.msgCount > 0) {
                Text(
                    text = getFormattedTimestamp(threadedGroupItem.timestamp),
                    style = MaterialTheme.typography.caption
                )
            }
        } else {
            Text(
                text = strings.groupDissolved,
                style = MaterialTheme.typography.caption
            )
        }
    }
}
