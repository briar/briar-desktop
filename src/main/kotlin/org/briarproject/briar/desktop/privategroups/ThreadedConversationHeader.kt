package org.briarproject.briar.desktop.privategroups

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.contact.ContactDropDown
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ThreadedConversationHeader(
    privateGroupItem: PrivateGroupItem,
    onMakeIntroduction: () -> Unit,
) {
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }
    val outlineColor = MaterialTheme.colors.outline

    Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            ProfileCircle(36.dp, privateGroupItem.privateGroup.id.bytes)
            Text(
                privateGroupItem.privateGroup.name,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp),
                fontSize = 20.sp
            )
        }
        IconButton(
            onClick = { setExpanded(!isExpanded) },
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
        ) {
            Icon(Icons.Filled.MoreVert, i18n("access.contact.menu"), modifier = Modifier.size(24.dp))
            ContactDropDown(isExpanded, setExpanded, onMakeIntroduction)
        }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}
