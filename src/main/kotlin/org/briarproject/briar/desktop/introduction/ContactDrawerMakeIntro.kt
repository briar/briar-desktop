package org.briarproject.briar.desktop.introduction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.contact.ContactCard
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider

@Composable
fun ContactDrawerMakeIntro(
    viewModel: IntroductionViewModel,
    setInfoDrawer: (Boolean) -> Unit
) {
    if (!viewModel.secondScreen.value) {
        Surface {
            Column {
                Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                    IconButton(
                        onClick = { setInfoDrawer(false) },
                        Modifier.padding(horizontal = 11.dp).size(32.dp).align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Close, "close make intro screen")
                    }
                    Text(
                        text = "Introduce ${viewModel.firstContact.value!!.author.name} to",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider()
                LazyColumn {
                    items(viewModel.contactList) { contactItem ->
                        ContactCard(
                            contactItem,
                            { viewModel.setSecondContact(contactItem.contact) },
                            false
                        )
                    }
                }
            }
        }
    } else {
        Column {
            Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                IconButton(
                    onClick = viewModel::backToFirstScreen,
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
                    ProfileCircle(36.dp, viewModel.firstContact.value!!.author.id.bytes)
                    Text(viewModel.firstContact.value!!.author.name, Modifier.padding(top = 4.dp), Color.White, 16.sp)
                }
                Icon(Icons.Filled.SwapHoriz, "swap", modifier = Modifier.size(48.dp))
                Column(Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp, viewModel.secondContact.value!!.author.id.bytes)
                    Text(viewModel.secondContact.value!!.author.name, Modifier.padding(top = 4.dp), Color.White, 16.sp)
                }
            }
            Row(Modifier.padding(8.dp)) {
                TextField(
                    viewModel.introductionMessage.value,
                    viewModel::setIntroductionMessage,
                    placeholder = { Text(text = "Add a message (optional)") },
                )
            }
            Row(Modifier.padding(8.dp)) {
                TextButton(
                    onClick = { setInfoDrawer(false) },
                    Modifier.fillMaxWidth()
                ) {
                    Text("MAKE INTRODUCTION")
                }
            }
        }
    }
}
