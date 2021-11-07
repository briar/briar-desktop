package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ContactDropDown(
    expanded: Boolean,
    isExpanded: (Boolean) -> Unit,
    onMakeIntroduction: () -> Unit,
) {
    var connectionMode by remember { mutableStateOf(false) }
    var contactMode by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { isExpanded(false) },
    ) {
        DropdownMenuItem(onClick = { isExpanded(false); onMakeIntroduction() }) {
            Text(i18n("contacts.dropdown.introduction"), fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = {}) {
            Text(i18n("contacts.dropdown.disappearing"), fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = {}) {
            Text(i18n("contacts.dropdown.delete.all"), fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = { connectionMode = true; isExpanded(false) }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(i18n("contacts.dropdown.connections"), fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                Icon(Icons.Filled.ArrowRight, i18n("access.contacts.dropdown.connections.expand"), modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
        DropdownMenuItem(onClick = { contactMode = true; isExpanded(false) }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(i18n("contacts.dropdown.contact"), fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                Icon(Icons.Filled.ArrowRight, i18n("access.contacts.dropdown.contacts.expand"), modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
    if (connectionMode) {
        DropdownMenu(
            expanded = connectionMode,
            onDismissRequest = { connectionMode = false },
        ) {
            DropdownMenuItem(onClick = { false }) {
                Text(i18n("contacts.dropdown.connections.title"), fontSize = 12.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text(i18n("contacts.dropdown.connections.bluetooth"), fontSize = 14.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text(i18n("contacts.dropdown.connections.removable"), fontSize = 14.sp)
            }
        }
    }
    if (contactMode) {
        DropdownMenu(
            expanded = contactMode,
            onDismissRequest = { contactMode = false },
        ) {
            DropdownMenuItem(onClick = { false }) {
                Text(i18n("contacts.dropdown.contact.title"), fontSize = 12.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text(i18n("contacts.dropdown.contact.change"), fontSize = 14.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text(i18n("contacts.dropdown.contact.delete"), fontSize = 14.sp)
            }
        }
    }
}
