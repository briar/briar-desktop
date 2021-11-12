package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp

@Composable
fun PrivateGroupCard(
    privateGroupItem: PrivateGroupItem,
    onSel: () -> Unit,
    selected: Boolean,
) {
    val bgColor = if (selected) MaterialTheme.colors.selectedCard else MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarPrimary = MaterialTheme.colors.primary
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE).clickable(onClick = onSel),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
                // TODO Pull profile pictures
                ProfileCircle(36.dp, privateGroupItem.privateGroup.id.bytes)
                // Draw notification badges
                if (privateGroupItem.unread > 0) {
                    Canvas(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onDraw = {
                            val size = 10.dp.toPx()
                            withTransform({ translate(left = -6f, top = -12f) }) {
                                drawCircle(color = outlineColor, radius = (size + 2.dp.toPx()) / 2f)
                                drawCircle(color = briarPrimary, radius = size / 2f)
                            }
                        }
                    )
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)) {
                    Text(
                        privateGroupItem.privateGroup.name,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                    )
                    Text(
                        if (privateGroupItem.isEmpty) i18n("contacts.card.nothing") else getFormattedTimestamp(privateGroupItem.timestamp),
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
        }
    }
    HorizontalDivider()
}
