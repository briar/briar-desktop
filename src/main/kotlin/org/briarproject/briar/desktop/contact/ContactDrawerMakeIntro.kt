package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider

@Composable
fun ContactDrawerMakeIntro(contact: Contact, contacts: List<Contact>, setInfoDrawer: (Boolean) -> Unit) {
    var introNextPg by remember { mutableStateOf(false) }
    val (introContact, onCancelSel) = remember { mutableStateOf(contact) }
    if (!introNextPg) {
        Surface {
            Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                IconButton(
                    onClick = { setInfoDrawer(false) },
                    Modifier.padding(horizontal = 11.dp).size(32.dp).align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.Close, "close make intro screen")
                }
                Text(
                    text = "Introduce " + contact.author.name + " to:",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            HorizontalDivider()
            Column(Modifier.verticalScroll(rememberScrollState())) {
                for (c in contacts) {
                    if (c.id != contact.id) {
                        // todo: refactor to use contactItem in IntroductionViewModel
                        //ContactCard(c, { onCancelSel(c); introNextPg = true }, false, false)
                    }
                }
            }
        }
    } else {
        Column {
            Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                IconButton(
                    onClick = { introNextPg = false },
                    Modifier.padding(horizontal = 11.dp).size(32.dp).align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.ArrowBack, "go back to make intro contact screen", tint = Color.White)
                }
                Text(
                    text = "Introduce Contacts",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
                Column(Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp, contact.author.id.bytes)
                    Text(contact.author.name, Modifier.padding(top = 4.dp), Color.White, 16.sp)
                }
                Icon(Icons.Filled.SwapHoriz, "swap", modifier = Modifier.size(48.dp))
                Column(Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp, introContact.author.id.bytes)
                    Text(introContact.author.name, Modifier.padding(top = 4.dp), Color.White, 16.sp)
                }
            }
            var introText by remember { mutableStateOf(TextFieldValue("")) }
            Row(Modifier.padding(8.dp)) {
                TextField(
                    introText,
                    { introText = it },
                    placeholder = { Text(text = "Add a message (optional)") },
                )
            }
            Row(Modifier.padding(8.dp)) {
                TextButton(
                    onClick = { setInfoDrawer(false); introNextPg = false; },
                    Modifier.fillMaxWidth()
                ) {
                    Text("MAKE INTRODUCTION")
                }
            }
        }
    }
}
