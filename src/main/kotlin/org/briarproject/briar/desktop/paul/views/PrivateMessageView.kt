package org.briarproject.briar.desktop.paul.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons.Filled
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.ContactExistsException
import org.briarproject.bramble.api.db.PendingContactExistsException
import org.briarproject.bramble.api.identity.AuthorConstants
import org.briarproject.bramble.util.StringUtils
import org.briarproject.briar.api.conversation.ConversationMessageHeader
import org.briarproject.briar.desktop.CTM
import org.briarproject.briar.desktop.CVM
import org.briarproject.briar.desktop.MM
import org.briarproject.briar.desktop.chat.Chat
import org.briarproject.briar.desktop.chat.ChatHistoryConversationVisitor
import org.briarproject.briar.desktop.chat.ConversationMessageHeaderComparator
import org.briarproject.briar.desktop.chat.SimpleMessage
import org.briarproject.briar.desktop.chat.UiState
import org.briarproject.briar.desktop.paul.theme.DarkColors
import org.briarproject.briar.desktop.paul.theme.awayMsgBubble
import org.briarproject.briar.desktop.paul.theme.divider
import org.briarproject.briar.desktop.paul.theme.localMsgBubble
import org.briarproject.briar.desktop.paul.theme.outline
import org.briarproject.briar.desktop.paul.theme.selectedCard
import org.briarproject.briar.desktop.paul.theme.surfaceVariant
import java.security.GeneralSecurityException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Collections

val HEADER_SIZE = 56.dp
val CONTACTLIST_WIDTH = 275.dp

// Right drawer state
enum class ContactInfoDrawerState {
    MakeIntro,
    ConnectBT,
    ConnectRD
}

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.divider, thickness = 1.dp, modifier = modifier.fillMaxWidth())
}

@Composable
fun VerticalDivider() {
    Divider(color = MaterialTheme.colors.divider, modifier = Modifier.fillMaxHeight().width(1.dp))
}

@Composable
fun PrivateMessageView(
    contact: Contact,
    contacts: List<Contact>,
    onContactSelect: (Contact) -> Unit
) {
    val (isDialogVisible, setDialogVisibility) = remember { mutableStateOf(false) }
    val (dropdownExpanded, setExpanded) = remember { mutableStateOf(false) }
    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(ContactInfoDrawerState.MakeIntro) }
    AddContactDialog(isDialogVisible, setDialogVisibility)
    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(contact, contacts, onContactSelect, setDialogVisibility)
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Conversation(
                contact,
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

@Composable
fun SearchTextField(searchValue: String, onValueChange: (String) -> Unit, onContactAdd: (Boolean) -> Unit) {
    TextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colors.onSurface),
        placeholder = { Text("Contacts") },
        shape = RoundedCornerShape(0.dp),
        leadingIcon = {
            val padding = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 12.dp)
            Icon(Filled.Search, "search contacts", padding)
        },
        trailingIcon = {
            IconButton(
                onClick = { onContactAdd(true) },
                modifier = Modifier.padding(end = 10.dp).size(32.dp)
                    .background(MaterialTheme.colors.primary, CircleShape)
            ) {
                Icon(Filled.PersonAdd, "add contact", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ContactCard(
    contact: Contact,
    selContact: Contact,
    onSel: (Contact) -> Unit,
    selected: Boolean,
    drawNotifications: Boolean
) {
    val bgColor = if (selected) MaterialTheme.colors.selectedCard else MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarPrimary = MaterialTheme.colors.primary
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().height(HEADER_SIZE).clickable(onClick = { onSel(contact) }),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
                // TODO Pull profile pictures
                ProfileCircle(36.dp, contact.author.id.bytes)
                // Draw notification badges
                if (drawNotifications) {
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
                        contact.author.name,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                    )
                    val latestMsgTime = CVM.current.getGroupCount(contact.id).latestMsgTime
                    Text(
                        getFormattedTimestamp(latestMsgTime),
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
            Canvas(
                modifier = Modifier.padding(start = 32.dp, end = 18.dp).size(22.dp).align(Alignment.CenterVertically),
                onDraw = {
                    val size = 16.dp.toPx()
                    drawCircle(color = outlineColor, radius = size / 2f)
                    // TODO check if contact online
                    if (true) {
                        drawCircle(color = briarSecondary, radius = 14.dp.toPx() / 2f)
                    } else {
                        drawCircle(color = briarSurfaceVar, radius = 14.dp.toPx() / 2f)
                    }
                }
            )
        }
    }
    HorizontalDivider()
}

fun getFormattedTimestamp(timestamp: Long): String {
    val messageTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()
    )
    val currentTime = LocalDateTime.now()
    val difference = ChronoUnit.MINUTES.between(messageTime, currentTime)

    val formatter = if (difference < 1440) { // = 1 day
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    } else {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    }
    return messageTime.format(formatter)
}

@Composable
fun ContactList(
    contact: Contact,
    contacts: List<Contact>,
    onContactSelect: (Contact) -> Unit,
    onContactAdd: (Boolean) -> Unit
) {
    var searchValue by remember { mutableStateOf("") }
    val filteredContacts = if (searchValue.isEmpty()) {
        ArrayList(contacts)
    } else {
        val resultList = ArrayList<Contact>()
        for (c in contacts) {
            if (c.author.name.lowercase().contains(searchValue.lowercase())) {
                resultList.add(c)
            }
        }
        resultList
    }
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
            ) {
                SearchTextField(searchValue, onValueChange = { searchValue = it }, onContactAdd)
            }
        },
        content = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                for (c in filteredContacts) {
                    ContactCard(c, contact, onContactSelect, contact.id == c.id, true)
                }
            }
        },
    )
}

@Composable
fun TextBubble(m: SimpleMessage) {
    if (m.local) {
        TextBubble(
            m,
            Alignment.End,
            MaterialTheme.colors.localMsgBubble,
            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp)
        )
    } else {
        TextBubble(
            m,
            Alignment.Start,
            MaterialTheme.colors.awayMsgBubble,
            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp)
        )
    }
}

@Composable
fun TextBubble(m: SimpleMessage, alignment: Alignment.Horizontal, color: Color, shape: RoundedCornerShape) {
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(fraction = 0.8f).align(alignment)) {
            Card(Modifier.align(alignment), backgroundColor = color, shape = shape) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    Text(m.message, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(m.time, Modifier.padding(end = 4.dp), fontSize = 10.sp)
                        if (m.delivered) {
                            val modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            Icon(Filled.DoneAll, "sent", modifier)
                        } else {
                            val modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                            Icon(Filled.Schedule, "sending", modifier)
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
                contentPadding = PaddingValues(8.dp)
            ) {
                items(chat.data.messages) { m -> TextBubble(m) }
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
                Icon(Filled.ArrowRight, "connections", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
        DropdownMenuItem(onClick = { contactMode = true; isExpanded(false) }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Contact", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                Icon(Filled.ArrowRight, "connections", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
    if (connectionMode) {
        DropdownMenu(
            expanded = connectionMode,
            onDismissRequest = { connectionMode = false },
        ) {
            DropdownMenuItem(onClick = { false }) {
                Text("Connections", fontSize = 12.sp)
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
        ) {
            DropdownMenuItem(onClick = { false }) {
                Text("Contact", fontSize = 12.sp)
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
            Icon(Filled.MoreVert, "contact info", modifier = Modifier.size(24.dp))
            ContactDropDown(expanded, isExpanded, setInfoDrawer)
        }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun ConversationInput() {
    var text by remember { mutableStateOf("") }
    Column {
        HorizontalDivider()
        TextField(
            value = text,
            onValueChange = { text = it },
            maxLines = 10,
            textStyle = TextStyle(fontSize = 16.sp, lineHeight = 16.sp),
            placeholder = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.background,
                unfocusedIndicatorColor = MaterialTheme.colors.background
            ),
            leadingIcon = {
                IconButton(
                    onClick = {},
                    Modifier.padding(4.dp).size(32.dp)
                        .background(MaterialTheme.colors.primary, CircleShape),
                ) {
                    Icon(Filled.Add, "add attachment", Modifier.size(24.dp), Color.White)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { }, modifier = Modifier.padding(4.dp).size(32.dp),
                ) {
                    Icon(
                        Filled.Send,
                        "send message",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewMsgInput() {
    MaterialTheme(colors = DarkColors) {
        Surface {
            ConversationInput()
        }
    }
}

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
                    Icon(Filled.Close, "close make intro screen")
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
                        ContactCard(c, contact, { onCancelSel(c); introNextPg = true }, false, false)
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
                    Icon(Filled.ArrowBack, "go back to make intro contact screen", tint = Color.White)
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
                Icon(Filled.SwapHoriz, "swap", modifier = Modifier.size(48.dp))
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

@Composable
fun ContactInfoDrawer(
    contact: Contact,
    contacts: List<Contact>,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    when (drawerState) {
        ContactInfoDrawerState.MakeIntro -> ContactDrawerMakeIntro(contact, contacts, setInfoDrawer)
    }
}

@Composable
fun Conversation(
    contact: Contact,
    contacts: List<Contact>,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    infoDrawer: Boolean,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val animatedInfoDrawerOffsetX by animateDpAsState(if (infoDrawer) (-275).dp else 0.dp)
        Scaffold(
            topBar = { ConversationHeader(contact, expanded, setExpanded, setInfoDrawer) },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    val chat = ChatState(contact.id)
                    DrawTextBubbles(chat.value)
                }
            },
            bottomBar = { ConversationInput() },
        )
        if (infoDrawer) {
            // TODO Find non-hacky way of setting scrim on entire app
            Box(
                Modifier.offset(-(CONTACTLIST_WIDTH + SIDEBAR_WIDTH))
                    .requiredSize(maxWidth + CONTACTLIST_WIDTH + SIDEBAR_WIDTH, maxHeight)
                    .background(Color(0, 0, 0, 100))
                    .clickable(
                        // prevent visual indication
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { setInfoDrawer(false) }
            )
            Column(
                modifier = Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH)
                    .offset(maxWidth + animatedInfoDrawerOffsetX)
                    .background(
                        MaterialTheme.colors.surfaceVariant,
                        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                    )
            ) {
                ContactInfoDrawer(contact, contacts, setInfoDrawer, drawerState)
            }
        }
    }
}

@Composable
fun ChatState(id: ContactId): MutableState<UiState<Chat>> {
    val state: MutableState<UiState<Chat>> = remember { mutableStateOf(UiState.Loading) }
    val messagingManager = MM.current
    val conversationManager = CVM.current

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
