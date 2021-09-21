package org.briarproject.briar.desktop.paul.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.v1.DialogProperties
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.briar.api.conversation.ConversationMessageHeader
import org.briarproject.briar.desktop.CM
import org.briarproject.briar.desktop.MM
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
import java.util.Collections

val HEADER_SIZE = 56.dp

// Right drawer state
enum class ContactInfoDrawerState {
    MakeIntro,
    ConnectBT,
    ConnectRD
}

@Composable
fun PrivateMessageView(
    currContact: Contact,
    contacts: List<Contact>,
    onContactSelect: (Contact) -> Unit
) {
    val (addContactDialog, onContactAdd) = remember { mutableStateOf(false) }
    val (dropdownExpanded, setExpanded) = remember { mutableStateOf(false) }
    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(ContactInfoDrawerState.MakeIntro) }
    AddContactDialog(addContactDialog, onContactAdd)
    Divider(color = divider, modifier = Modifier.fillMaxHeight().width(1.dp))
    ContactList(currContact, contacts, onContactSelect, onContactAdd)
    Column(modifier = Modifier.fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Divider(color = divider, modifier = Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).fillMaxHeight().background(color = darkGray)) {
                DrawMessageRow(
                    currContact,
                    contacts,
                    dropdownExpanded,
                    setExpanded,
                    infoDrawer,
                    setInfoDrawer,
                    contactDrawerState
                )
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
fun SearchTextField(searchValue: String, onValueChange: (String) -> Unit, onContactAdd: (Boolean) -> Unit) {
    BasicTextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier.padding(horizontal = 8.dp),
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .background(darkGray, CircleShape)
                    .border(1.dp, divider, CircleShape)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Filled.Search,
                    "search contacts",
                    tint = Color.White,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp)
                )
                Box(Modifier.width(132.dp).padding(vertical = 8.dp, horizontal = 2.dp)) {
                    if (searchValue.isEmpty()) {
                        Text("Contacts", color = Color.Gray)
                    }
                    innerTextField()
                }
                IconButton(
                    onClick = { onContactAdd(true) },
                    modifier = Modifier.padding(end = 4.dp).size(32.dp).background(
                        briarBlueMsg, CircleShape
                    )
                ) {
                    Icon(Icons.Filled.PersonAdd, "add contact", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        },
        cursorBrush = SolidColor(Color.White),
    )
}

@Composable
fun ContactCard(contact: Contact, selContact: Contact, onSel: (Contact) -> Unit, drawDetails: Boolean) {
    var bgColor = briarBlack
    if (selContact.id == contact.id && drawDetails) {
        bgColor = darkGray
    }
    Row(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE).background(bgColor)
            .clickable(onClick = { onSel(contact) }),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
            // TODO Pull profile pictures
            Image(
                bitmap = imageFromResource("images/profile_images/p0.png"),
                "image",
                modifier = Modifier.size(36.dp).align(Alignment.CenterVertically).clip(
                    CircleShape
                ).border(2.dp, color = Color.White, CircleShape)
            )
            // Draw notification badges
            if (drawDetails) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onDraw = {
                        val size = 10.dp.toPx()
                        withTransform({ translate(left = -6f, top = -12f) }) {
                            drawCircle(
                                color = Color.White,
                                radius = (size + 2.dp.toPx()) / 2f,
                            )
                            drawCircle(
                                color = briarBlueMsg,
                                radius = size / 2f,
                            )
                        }
                    }
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)) {
                Text(
                    contact.author.name,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                )
                // TODO add proper last message time
                Text(
                    "10 min ago",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
        androidx.compose.foundation.Canvas(
            modifier = Modifier.padding(start = 32.dp, end = 18.dp).size(22.dp).align(Alignment.CenterVertically),
            onDraw = {
                val size = 16.dp.toPx()
                drawCircle(
                    color = Color.White,
                    radius = size / 2f
                )
                // TODO check if contact online
                if (true) {
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
fun ContactList(
    currContact: Contact,
    contacts: List<Contact>,
    onContactSelect: (Contact) -> Unit,
    onContactAdd: (Boolean) -> Unit
) {
    var searchValue by remember { mutableStateOf("") }
    var filteredContacts = ArrayList<Contact>()
    filteredContacts = if (searchValue.isEmpty()) {
        ArrayList(contacts)
    } else {
        val resultList = ArrayList<Contact>()
        for (c in contacts) {
            if (c.author.name.lowercase().contains(searchValue.lowercase())
            ) {
                resultList.add(c)
            }
        }
        resultList
    }
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(246.dp),
        backgroundColor = briarBlack,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().background(briarBlack),
            ) {
                Row(Modifier.height(HEADER_SIZE), verticalAlignment = Alignment.CenterVertically) {
                    SearchTextField(searchValue, onValueChange = { searchValue = it }, onContactAdd)
                }
                Divider(color = divider, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            }
        },
        content = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                for (c in filteredContacts) {
                    ContactCard(c, currContact, onContactSelect, true)
                }
            }
        },
    )
}

@Composable
fun TextBubble(m: SimpleMessage) {
    Column(Modifier.fillMaxWidth()) {
        if (m.local) {
            Column(Modifier.fillMaxWidth(fraction = 0.8f).align(Alignment.End)) {
                Column(
                    Modifier.background(
                        briarBlueMsg,
                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp)
                    ).padding(8.dp).align(Alignment.End)
                ) {
                    Text(m.message, fontSize = 14.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(m.time, Modifier.padding(end = 4.dp), fontSize = 10.sp, color = Color.LightGray)
                        if (m.delivered) {
                            Icon(
                                Icons.Filled.DoneAll,
                                "sent",
                                tint = Color.LightGray,
                                modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Schedule,
                                "sending",
                                tint = Color.LightGray,
                                modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxWidth(fraction = 0.9f).align(Alignment.Start)) {
                Column(
                    Modifier.background(
                        briarGrayMsg,
                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp)
                    ).padding(8.dp).align(Alignment.Start)
                ) {
                    Text(m.message, fontSize = 14.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(m.time, Modifier.padding(end = 4.dp), fontSize = 10.sp, color = Color.LightGray)
                        if (m.delivered) {
                            Icon(
                                Icons.Filled.DoneAll,
                                "sent",
                                tint = Color.LightGray,
                                modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            )
                        } else {
                            Icon(
                                Icons.Filled.Schedule,
                                "sending",
                                tint = Color.LightGray,
                                modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp)
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
fun ContactDropDown(
    expanded: Boolean,
    isExpanded: (Boolean) -> Unit,
    setInfoDrawer: (Boolean) -> Unit
) {
    var connectionMode by remember { mutableStateOf(false) }
    var contactMode by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { isExpanded(false) },
        modifier = Modifier.background(briarBlack)
    ) {
        DropdownMenuItem(onClick = { setInfoDrawer(true); isExpanded(false) }) {
            Text("Make Introduction", fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = {}) {
            Text("Disappearing Messages", fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = {}) {
            Text("Delete all messages", fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = { connectionMode = true; isExpanded(false) }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Connections", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                Icon(Icons.Filled.ArrowRight, "connections", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
        DropdownMenuItem(onClick = { contactMode = true; isExpanded(false) }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Contact", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                Icon(Icons.Filled.ArrowRight, "connections", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
    if (connectionMode) {
        DropdownMenu(
            expanded = connectionMode,
            onDismissRequest = { connectionMode = false },
            modifier = Modifier.background(briarBlack)
        ) {
            DropdownMenuItem(onClick = { false }) {
                Text("Connections", color = lightGray, fontSize = 12.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text("Connect via Bluetooth", fontSize = 14.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text("Connect via Removable Device", fontSize = 14.sp)
            }
        }
    }
    if (contactMode) {
        DropdownMenu(
            expanded = contactMode,
            onDismissRequest = { contactMode = false },
            modifier = Modifier.background(briarBlack)
        ) {
            DropdownMenuItem(onClick = { false }) {
                Text("Contact", color = lightGray, fontSize = 12.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text("Change contact name", fontSize = 14.sp)
            }
            DropdownMenuItem(onClick = { false }) {
                Text("Delete contact", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MsgColumnHeader(
    contact: Contact,
    expanded: Boolean,
    isExpanded: (Boolean) -> Unit,
    setInfoDrawer: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            Image(
                // TODO Fix profile picture resources
                bitmap = imageFromResource("images/profile_images/p0.png"),
                "sel_contact_prof",
                modifier = Modifier.size(36.dp).align(
                    Alignment.CenterVertically
                ).clip(
                    CircleShape
                ).border(2.dp, color = Color.White, CircleShape)
            )
            androidx.compose.foundation.Canvas(
                modifier = Modifier.align(Alignment.CenterVertically),
                onDraw = {
                    val size = 10.dp.toPx()
                    // TODO hook up online indicator logic
                    val onlineColor = if (true) briarGreen else briarBlack
                    withTransform({ translate(left = -6f, top = 12f) }) {
                        drawCircle(
                            color = Color.White,
                            radius = (size + 2.dp.toPx()) / 2f,
                        )
                        drawCircle(
                            color = onlineColor,
                            radius = size / 2f,
                        )
                    }
                }
            )
            Text(
                contact.author.name,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp),
                fontSize = 20.sp
            )
        }
        IconButton(
            onClick = { isExpanded(!expanded) },
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
        ) {
            Icon(Icons.Filled.MoreVert, "contact info", tint = Color.White, modifier = Modifier.size(24.dp))
            ContactDropDown(expanded, isExpanded, setInfoDrawer)
        }
        Divider(color = divider, thickness = 1.dp, modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter))
    }
}

@Composable
fun MsgInput(currContact: Contact) {
    var text by remember { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            maxLines = 10,
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp, lineHeight = 16.sp),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .background(darkGray, RoundedCornerShape(size = 20.dp))
                        .border(1.dp, divider, RoundedCornerShape(size = 20.dp))
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    IconButton(
                        onClick = {},
                        Modifier.padding(4.dp).size(32.dp).align(Alignment.TopStart)
                            .background(briarBlueMsg, CircleShape),
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            "add attachment",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Box(
                        Modifier.padding(vertical = 8.dp, horizontal = 48.dp).align(Alignment.Center).fillMaxWidth()
                    ) {
                        if (text.isEmpty()) {
                            Text("Message", color = Color.Gray)
                        }
                        innerTextField()
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.padding(4.dp).size(32.dp).align(Alignment.TopEnd),
                    ) {
                        Icon(Icons.Filled.Send, "send message", tint = briarGreen, modifier = Modifier.size(24.dp))
                    }
                }
            },
            cursorBrush = SolidColor(Color.White),
        )
    }
}

@Composable
fun ContactDrawerMakeIntro(currContact: Contact, contacts: List<Contact>, setInfoDrawer: (Boolean) -> Unit) {
    var introNextPg by remember { mutableStateOf(false) }
    val (introContact, onCancelSel) = remember { mutableStateOf(currContact) }
    if (!introNextPg) {
        Column() {
            Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                IconButton(
                    onClick = { setInfoDrawer(false) },
                    Modifier.padding(horizontal = 11.dp).size(32.dp).align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.Close, "close make intro screen", tint = Color.White)
                }
                Text(
                    text = "Introduce " + currContact.author.name + " to:",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Divider(color = divider, modifier = Modifier.fillMaxWidth().height(1.dp))
            Column(Modifier.verticalScroll(rememberScrollState())) {
                for (c in contacts) {
                    if (c.id != currContact.id) {
                        ContactCard(c, currContact, { onCancelSel(c); introNextPg = true }, false)
                    }
                }
            }
        }
    } else {
        Column() {
            Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                IconButton(
                    onClick = { introNextPg = false },
                    Modifier.padding(horizontal = 11.dp).size(32.dp).align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Filled.ArrowBack, "go back to make intro contact screen", tint = Color.White)
                }
                Text(
                    text = "Introduce Contacts",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            // Divider(color = divider, modifier = Modifier.fillMaxWidth().height(1.dp) )
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
                Column(Modifier.align(Alignment.CenterVertically)) {
                    Image(
                        // TODO Proper profile pic
                        bitmap = imageFromResource("images/profile_images/p0.png"),
                        "image",
                        modifier = Modifier.size(40.dp).align(Alignment.CenterHorizontally).clip(
                            CircleShape
                        ).border(2.dp, color = Color.White, CircleShape)
                    )
                    Text(
                        currContact.author.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(Icons.Filled.SwapHoriz, "swap", tint = Color.White, modifier = Modifier.size(48.dp))
                Column(Modifier.align(Alignment.CenterVertically)) {
                    // TODO Profile pic again
                    Image(
                        bitmap = imageFromResource("images/profile_images/p0.png"),
                        "image",
                        modifier = Modifier.size(40.dp).align(Alignment.CenterHorizontally).clip(
                            CircleShape
                        ).border(2.dp, color = Color.White, CircleShape)
                    )
                    Text(
                        introContact.author.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            var introText by remember { mutableStateOf(TextFieldValue("")) }
            Row(Modifier.padding(8.dp)) {
                TextField(
                    introText,
                    { introText = it },
                    placeholder = { Text(text = "Add a message (optional)") },
                    textStyle = TextStyle(color = Color.White)
                )
            }
            Row(Modifier.padding(8.dp)) {
                TextButton(
                    onClick = { setInfoDrawer(false); introNextPg = false; },
                    Modifier.fillMaxWidth().background(briarDarkGray)
                ) {
                    Text("MAKE INTRODUCTION")
                }
            }
        }
    }
}

@Composable
fun ContactInfoDrawer(
    currContact: Contact,
    contacts: List<Contact>,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    Row() {
        when (drawerState) {
            ContactInfoDrawerState.MakeIntro -> ContactDrawerMakeIntro(currContact, contacts, setInfoDrawer)
        }
    }
}

@Composable
fun DrawMessageRow(
    currContact: Contact,
    contacts: List<Contact>,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    infoDrawer: Boolean,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val animatedInfoDrawerOffsetX by animateDpAsState(
            if (infoDrawer) {
                -275.dp
            } else {
                0.dp
            }
        )
        Scaffold(
            topBar = { MsgColumnHeader(currContact, expanded, setExpanded, setInfoDrawer) },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    val chat = ChatState(currContact.id)
                    DrawTextBubbles(chat.value)
                }
            },
            bottomBar = { MsgInput(currContact) },
            backgroundColor = darkGray,
            modifier = Modifier.offset()
        )
        if (infoDrawer) {
            // TODO Find non-hacky way of setting scrim
            // This dims the entire app while the drawer is open by making a very very large slightly see-through black box
            Box(Modifier.requiredSize(maxWidth, maxHeight).background(Color(0, 0, 0, 100)))
            Column(
                modifier = Modifier.fillMaxHeight().width(275.dp).offset(maxWidth + animatedInfoDrawerOffsetX)
                    .background(briarBlack, RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
            ) {
                ContactInfoDrawer(currContact, contacts, setInfoDrawer, drawerState)
            }
        }
    }
}

@Composable
fun ChatState(id: ContactId): MutableState<UiState<Chat>> {
    val state: MutableState<UiState<Chat>> = remember { mutableStateOf(UiState.Loading) }
    val messagingManager = MM.current
    val conversationManager = CM.current

    DisposableEffect(id) {
        state.value = UiState.Loading
        val chat = Chat()
        val visitor = ChatHistoryConversationVisitor(chat, messagingManager)
        val messageHeaders: List<ConversationMessageHeader> = ArrayList(conversationManager.getMessageHeaders(id))
        Collections.sort(messageHeaders, ConversationMessageHeaderComparator())
        // Reverse order here because we're using reverseLayout=true on the LazyColumn to display items
        // from bottom to top
        Collections.reverse(messageHeaders)
        for (header in messageHeaders) {
            header.accept(visitor)
        }
        state.value = UiState.Success(chat)
        onDispose { }
    }

    return state
}
