package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.contact.ContactDropDown
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider

@Composable
fun ConversationHeader(
    contact: Contact,
    expanded: Boolean,
    isExpanded: (Boolean) -> Unit,
    setInfoDrawer: (Boolean) -> Unit
) {
    // TODO hook up online indicator logic
    val onlineColor = MaterialTheme.colors.secondary
    val outlineColor = MaterialTheme.colors.outline

    Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            ProfileCircle(36.dp, contact.author.id.bytes)
            Canvas(
                modifier = Modifier.align(Alignment.CenterVertically),
                onDraw = {
                    val size = 10.dp.toPx()
                    withTransform({ translate(left = -6f, top = 12f) }) {
                        drawCircle(color = outlineColor, radius = (size + 2.dp.toPx()) / 2f)
                        drawCircle(color = onlineColor, radius = size / 2f)
                    }
                }
            )
            Text(
                contact.author.name,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp),
                fontSize = 20.sp
            )
        }
        IconButton(
            onClick = { isExpanded(!expanded) },
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
        ) {
            Icon(Icons.Filled.MoreVert, "contact info", modifier = Modifier.size(24.dp))
            ContactDropDown(expanded, isExpanded, setInfoDrawer)
        }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}
