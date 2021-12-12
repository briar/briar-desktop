package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.desktop.theme.onWarning
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.CONTACTLIST_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import java.time.Instant
import javax.print.attribute.standard.MediaSize

fun main() = preview(
) {
    Column(Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH)) {
        OtherContactCard("Nick Name")
        PendingContactCard()
        OtherContactCard("Alice Anne")
        FailedAddContactCard()
    }
}

@Composable
fun PendingContactCard(
) {
    val bgColor = MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    CircularProgressIndicator(Modifier.size(36.dp), color = MaterialTheme.colors.onWarning)
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)) {
                    Text(
                        "Tim Test",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                    )
                    Text(
                        "15min ago",
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxSize().align(Alignment.CenterVertically).padding(end = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Rendevouz in progress...", color = MaterialTheme.colors.onWarning, fontSize = 10.sp, modifier = Modifier.width(88.dp).align(Alignment.CenterVertically))
                IconButton({}, Modifier.size(36.dp).align(Alignment.CenterVertically)) {
                    Icon(Icons.Filled.Delete, "stop rendevouz",)
                }
            }
        }
    }
    HorizontalDivider()
}

@Composable
fun FailedAddContactCard(
) {
    val bgColor = MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp)) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(Icons.Filled.Error, "failed rendevouz", tint = MaterialTheme.colors.error, modifier = Modifier.size(36.dp))
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)) {
                    Text(
                        "Tim Test",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                    )
                    Text(
                        "Rendevouz Failed",
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Start),
                        color = MaterialTheme.colors.error,
                    )
                }
            }
            Row(modifier = Modifier.fillMaxSize().align(Alignment.CenterVertically)) {
                TextButton({}, Modifier.align(Alignment.CenterVertically).padding(start = 9.dp)) {
                    Text("RETRY", fontSize = 12.sp)
                }
                IconButton({}, Modifier.size(36.dp).align(Alignment.CenterVertically)) {
                    Icon(Icons.Filled.Delete, "stop rendevouz",)
                }
            }
        }
    }
    HorizontalDivider()
}

@Composable
fun OtherContactCard(name: String
) {
    val bgColor = MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp)
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)) {
                    Text(
                        name,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                    )
                    Text(
                        "15min ago",
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
            Canvas(
                modifier = Modifier.padding(start = 32.dp, end = 18.dp).size(22.dp).align(Alignment.CenterVertically),
                onDraw = {
                    val size = 16.dp
                    drawCircle(color = outlineColor, radius = size.toPx() / 2f)
                    drawCircle(
                        color = briarSecondary,
                        radius = (size - 2.dp).toPx() / 2f
                    )
                }
            )
        }
    }
    HorizontalDivider()
}
