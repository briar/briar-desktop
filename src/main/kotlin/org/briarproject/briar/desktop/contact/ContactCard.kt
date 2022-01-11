package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.MessageCounter
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import java.time.Instant

fun main() = preview(
    "name" to "Paul",
    "alias" to "UI Master",
    "isConnected" to true,
    "isEmpty" to false,
    "unread" to 3,
    "timestamp" to Instant.now().toEpochMilli(),
    "selected" to false,
) {
    ContactCard(
        ContactItem(
            idWrapper = RealContactIdWrapper(ContactId(0)),
            authorId = AuthorId(getRandomIdPersistent()),
            name = getStringParameter("name"),
            alias = getStringParameter("alias"),
            isConnected = getBooleanParameter("isConnected"),
            isEmpty = getBooleanParameter("isEmpty"),
            unread = getIntParameter("unread"),
            timestamp = getLongParameter("timestamp"),
            avatar = null,
        ),
        {}, getBooleanParameter("selected")
    )
}

@Composable
fun ContactCard(
    contactItem: BaseContactItem,
    onSel: () -> Unit,
    selected: Boolean,
    padding: PaddingValues = PaddingValues(0.dp),
) {
    val bgColor = if (selected) MaterialTheme.colors.selectedCard else MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = HEADER_SIZE).clickable(onClick = onSel),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)
                    .weight(1f, fill = false)
            ) {
                when (contactItem) {
                    is ContactItem -> {
                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                            ProfileCircle(36.dp, contactItem)
                            MessageCounter(
                                unread = contactItem.unread,
                                modifier = Modifier.align(Alignment.TopEnd).offset(6.dp, (-6).dp)
                            )
                        }
                        RealContactInfo(
                            contactItem = contactItem,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    is PendingContactItem -> {
                        ProfileCircle(36.dp)
                        PendingContactInfo(
                            contactItem = contactItem,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            if (contactItem is ContactItem)
                Canvas(
                    modifier = Modifier.padding(end = 18.dp).size(22.dp)
                        .align(Alignment.CenterVertically),
                    onDraw = {
                        val size = 16.dp
                        drawCircle(color = outlineColor, radius = size.toPx() / 2f)
                        drawCircle(
                            color = if (contactItem.isConnected) briarSecondary else briarSurfaceVar,
                            radius = (size - 2.dp).toPx() / 2f
                        )
                    }
                )
        }
    }
    HorizontalDivider()
}

@Composable
fun RealContactInfo(contactItem: ContactItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 12.dp)) {
        Text(
            contactItem.displayName,
            fontSize = 14.sp,
            maxLines = 3,
            overflow = Ellipsis,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
        )
        Text(
            if (contactItem.isEmpty) i18n("contacts.card.nothing") else getFormattedTimestamp(
                contactItem.timestamp
            ),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}

@Composable
fun PendingContactInfo(contactItem: PendingContactItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 12.dp)) {
        Text(
            contactItem.displayName,
            fontSize = 14.sp,
            maxLines = 3,
            overflow = Ellipsis,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
        )
        Text(
            getFormattedTimestamp(contactItem.timestamp),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}
