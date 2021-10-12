package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.ContactExistsException
import org.briarproject.bramble.api.db.PendingContactExistsException
import org.briarproject.bramble.api.identity.AuthorConstants
import org.briarproject.bramble.util.StringUtils
import org.briarproject.briar.desktop.CTM
import java.security.GeneralSecurityException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddContactDialog(isVisible: Boolean, setDialogVisibility: (Boolean) -> Unit) {
    if (!isVisible) {
        return
    }
    var contactAlias by remember { mutableStateOf("") }
    var contactLink by remember { mutableStateOf("") }
    val contactManager = CTM.current
    val ownLink = CTM.current.handshakeLink
    AlertDialog(
        onDismissRequest = { setDialogVisibility(false) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Text(
                        text = "Add Contact at a Distance",
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Contact's Link",
                        Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(contactLink, onValueChange = { contactLink = it }, modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Contact's Name",
                        Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(contactAlias, onValueChange = { contactAlias = it }, modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Your Link",
                        modifier = Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(
                        ownLink,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (ownLink.equals(contactLink)) {
                        println("Please enter contact's link, not your own")
                        setDialogVisibility(false)
                        return@Button
                    }
                    addPendingContact(contactManager, contactAlias, contactLink)
                    setDialogVisibility(false)
                },
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { setDialogVisibility(false) }
            ) {
                Text("Cancel", color = MaterialTheme.colors.onSurface)
            }
        },
        modifier = Modifier.size(600.dp, 300.dp),
    )
}

private fun addPendingContact(contactManager: ContactManager, alias: String, link: String) {
    if (aliasIsInvalid(alias)) {
        println("Alias is invalid")
        return
    }
    try {
        contactManager.addPendingContact(link, alias)
    } catch (e: FormatException) {
        println("Link is invalid")
        println(e.stackTrace)
    } catch (e: GeneralSecurityException) {
        println("Public key is invalid")
        println(e.stackTrace)
    }
    /*
    TODO: Warn user that the following two errors might be an attack

     Use `e.pendingContact.id.bytes` and `e.pendingContact.alias` to implement the following logic:
     https://code.briarproject.org/briar/briar-gtk/-/merge_requests/97

    */
    catch (e: ContactExistsException) {
        println("Contact already exists")
        println(e.stackTrace)
    } catch (e: PendingContactExistsException) {
        println("Pending Contact already exists")
        println(e.stackTrace)
    }
}

private fun aliasIsInvalid(alias: String): Boolean {
    val aliasUtf8 = StringUtils.toUtf8(alias)
    return aliasUtf8.isEmpty() || aliasUtf8.size > AuthorConstants.MAX_AUTHOR_NAME_LENGTH
}
