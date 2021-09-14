package org.briarproject.briar.desktop.paul.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.v1.DialogProperties
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.ConversationMessageHeader
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.desktop.chat.Chat
import org.briarproject.briar.desktop.chat.ChatHistoryConversationVisitor
import org.briarproject.briar.desktop.chat.ConversationMessageHeaderComparator
import org.briarproject.briar.desktop.chat.SimpleMessage
import org.briarproject.briar.desktop.chat.UiState
import org.briarproject.briar.desktop.paul.theme.briarBlack
import org.briarproject.briar.desktop.paul.theme.briarBlue
import org.briarproject.briar.desktop.paul.theme.briarBlueMsg
import org.briarproject.briar.desktop.paul.theme.briarDarkGray
import org.briarproject.briar.desktop.paul.theme.briarGrayMsg
import org.briarproject.briar.desktop.paul.theme.briarGreen
import org.briarproject.briar.desktop.paul.theme.darkGray
import org.briarproject.briar.desktop.paul.theme.divider
import org.briarproject.briar.desktop.paul.theme.lightGray
import java.util.*


val HEADER_SIZE = 66.dp

@Composable
fun PrivateMessageView(
    contacts: List<Contact>,
    uiContact: MutableState<Contact>,
    conversationManager: ConversationManager,
    messagingManager: MessagingManager
) {
    // Local State for managing the Add Contact Popup
    val (AddContactDialog, onCancelAdd) = remember { mutableStateOf(false) }
    AddContactDialog(AddContactDialog, onCancelAdd)
    Column(modifier = Modifier.fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Divider(color = divider, modifier = Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.fillMaxHeight().background(color = briarBlack).width(275.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(HEADER_SIZE).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Contacts",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    IconButton(
                        onClick = { onCancelAdd(true) },
                        modifier = Modifier.align(Alignment.CenterVertically).background(color = briarDarkGray)
                    ) {
                        Icon(Icons.Filled.Add, "add contact", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Divider(color = divider, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    for (c in contacts) {
                        ContactCard(uiContact, c)
                    }
                }
            }
            Divider(color = divider, modifier = Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).fillMaxHeight().background(color = darkGray)) {
                DrawMessageRow(uiContact.value, conversationManager, messagingManager)
            }
        }
    }
}

@Composable
fun AddContactDialog(isVisible: Boolean, onCancel: (Boolean) -> Unit) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                onCancel(false)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                        Text(
                            text = "Add Contact at a Distance",
                            fontSize = 24.sp,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Contact's Link",
                            Modifier.width(128.dp).align(Alignment.CenterVertically),
                            color = lightGray
                        )
                        TextField("", onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Contact's Name",
                            Modifier.width(128.dp).align(Alignment.CenterVertically),
                            color = lightGray
                        )
                        TextField("", onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Your Link",
                            modifier = Modifier.width(128.dp).align(Alignment.CenterVertically),
                            color = lightGray
                        )
                        TextField(
                            // TODO: use real code
                            "briar://ksdjlfgakslhjgaklsjdhglkasjdlk3j12h4lk2j3tkj4",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancel(false)
                    },
                    modifier = Modifier.background(briarGreen)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onCancel(false)
                    },
                    modifier = Modifier.background(briarBlack)
                ) {
                    Text("Cancel")
                }
            },

            backgroundColor = briarBlue,
            contentColor = Color.White,
            modifier = Modifier.border(1.dp, color = divider),
            properties = DialogProperties(resizable = false, undecorated = true, size = IntSize(600, 300))
        )
    }
}

@Composable
fun ContactCard(selContact: MutableState<Contact>, contact: Contact) {
    var bgColor = briarBlack
    if (selContact.value.id == contact.id) {
        bgColor = darkGray
    }
    Row(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE).background(bgColor)
            .clickable(onClick = {
                selContact.value = contact
            }),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
            Image(
                // TODO: use correct image
                // bitmap = imageFromResource("images/profile_images/" + contact.profile_pic),
                bitmap = imageFromResource("images/profile_images/p1.png"),
                "image",
                modifier = Modifier.size(40.dp).align(Alignment.CenterVertically).clip(
                    CircleShape
                ).border(2.dp, color = Color.White, CircleShape)
            )
            Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)) {
                Text(
                    contact.author.name,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                )
                Text(
                    "1 min",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
        androidx.compose.foundation.Canvas(
            modifier = Modifier.padding(horizontal = 29.dp).size(22.dp).align(Alignment.CenterVertically),
            onDraw = {
                val size = 16.dp.toPx()
                drawCircle(
                    color = Color.White,
                    radius = size / 2f
                )
                val online = true
                if (online) {
                    drawCircle(
                        color = briarGreen,
                        radius = 14.dp.toPx() / 2f
                    )
                } else {
                    drawCircle(
                        color = briarBlack,
                        radius = 14.dp.toPx() / 2f
                    )
                }
            }
        )
    }

    Divider(color = divider, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
}

@Composable
fun TextBubble(m: SimpleMessage) {
    Column(Modifier.fillMaxWidth()) {
        if (m.local) {
            Column(Modifier.fillMaxWidth(fraction = 0.9f).align(Alignment.End)) {
                Column(Modifier.background(briarBlueMsg).padding(8.dp).align(Alignment.End)) {
                    Text(m.message, fontSize = 14.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(m.time, Modifier.padding(end = 4.dp), fontSize = 10.sp, color = Color.LightGray)
                        if (m.delivered) {
                            Icon(
                                Icons.Filled.Check,
                                "sent",
                                tint = Color.LightGray,
                                modifier = Modifier.size(10.dp).align(Alignment.CenterVertically)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Send,
                                "sending",
                                tint = Color.LightGray,
                                modifier = Modifier.size(10.dp).align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxWidth(fraction = 0.9f).align(Alignment.Start)) {
                Column(Modifier.background(briarGrayMsg).padding(8.dp).align(Alignment.Start)) {
                    Text(m.message, fontSize = 14.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(m.time, Modifier.padding(end = 4.dp), fontSize = 10.sp, color = Color.LightGray)
                        if (m.delivered) {
                            Icon(
                                Icons.Filled.Check,
                                "sent",
                                tint = Color.LightGray,
                                modifier = Modifier.size(10.dp).align(Alignment.CenterVertically)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Send,
                                "sending",
                                tint = Color.LightGray,
                                modifier = Modifier.size(10.dp).align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawTextBubbles(chat: UiState<Chat>) {
    when (chat) {
        is UiState.Loading -> Loader()
        is UiState.Error -> Loader()
        is UiState.Success ->
            LazyColumn(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(chat.data.messages) { m ->
                    TextBubble(m)
                }
            }
    }
}

@Composable
fun Loader() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DrawMessageRow(
    uiContact: Contact,
    conversationManager: ConversationManager,
    messagingManager: MessagingManager
) {
    Box(Modifier.fillMaxHeight()) {
        Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
            Row(modifier = Modifier.align(Alignment.Center)) {
                Image(
                    // TODO: use correct image
                    // bitmap = imageFromResource("images/profile_images/" + UIContact.profile_pic),
                    bitmap = imageFromResource("images/profile_images/p2.png"),
                    "sel_contact_prof",
                    modifier = Modifier.size(36.dp).align(
                        Alignment.CenterVertically
                    ).clip(
                        CircleShape
                    ).border(2.dp, color = Color.White, CircleShape)
                )
                Text(
                    uiContact.author.name,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp),
                    fontSize = 24.sp
                )
            }
            IconButton(onClick = {}, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)) {
                Icon(Icons.Filled.MoreVert, "contact info", tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Divider(
                color = divider,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
            )
        }
        Box(Modifier.padding(top = HEADER_SIZE + 1.dp, bottom = HEADER_SIZE)) {
            val chat = ChatState(uiContact.id, conversationManager, messagingManager)
            DrawTextBubbles(chat.value)
        }
        var text by remember { mutableStateOf(TextFieldValue("")) }
        Box(Modifier.align(Alignment.BottomCenter).background(darkGray)) {
            OutlinedTextField(
                value = text,
                trailingIcon = { Icon(Icons.Filled.Send, "send message", tint = briarGreen) },
                leadingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = "add file") },
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp).fillMaxWidth(),
                label = { Text(text = "Message") },
                textStyle = TextStyle(color = Color.White),
                placeholder = { Text(text = "Your message to " + uiContact.author.name) },
                onValueChange = {
                    text = it
                },
            )
        }
    }
}

@Composable
fun ChatState(
    id: ContactId,
    conversationManager: ConversationManager,
    messagingManager: MessagingManager
): MutableState<UiState<Chat>> {
    val state: MutableState<UiState<Chat>> = remember { mutableStateOf(UiState.Loading) }

    DisposableEffect(id) {
        state.value = UiState.Loading
        val chat = Chat()
        val visitor = ChatHistoryConversationVisitor(chat, messagingManager)
        val messageHeaders: List<ConversationMessageHeader> = ArrayList(conversationManager.getMessageHeaders(id))
        Collections.sort(messageHeaders, ConversationMessageHeaderComparator())
        // FIXME: for some reason messages are displayed in reverse order
        Collections.reverse(messageHeaders)
        for (header in messageHeaders) {
            header.accept(visitor)
        }
        state.value = UiState.Success(chat)
        onDispose { }
    }

    return state
}