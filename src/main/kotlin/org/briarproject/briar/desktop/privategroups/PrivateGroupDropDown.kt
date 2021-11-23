package org.briarproject.briar.desktop.privategroups

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp

@Composable
fun PrivateGroupDropDown(
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
            Text("Member List", fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = {}) {
            Text("Reveal Contacts", fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = {}) {
            Text("Leave Group", fontSize = 14.sp, color = MaterialTheme.colors.error)
        }
    }
}
