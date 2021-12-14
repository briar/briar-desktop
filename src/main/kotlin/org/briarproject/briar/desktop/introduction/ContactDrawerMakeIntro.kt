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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.contact.ContactCard
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.viewmodel.viewModel
import java.util.Locale

@Composable
fun ContactDrawerMakeIntro(
    contactItem: ContactItem,
    closeInfoDrawer: () -> Unit,
    viewModel: IntroductionViewModel = viewModel(),
) {
    LaunchedEffect(contactItem) {
        viewModel.setFirstContact(contactItem)
    }
    if (!viewModel.secondScreen.value) {
        Surface {
            Column {
                Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                    IconButton(
                        onClick = closeInfoDrawer,
                        Modifier.padding(horizontal = 11.dp).size(32.dp).align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Close, i18n("access.introduction.close"))
                    }
                    Text(
                        text = i18nF("introduction.title_first", contactItem.displayName),
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider()
                LazyColumn {
                    items(viewModel.contactList) { contactItem ->
                        if (contactItem is ContactItem)
                            ContactCard(
                                contactItem,
                                { viewModel.setSecondContact(contactItem) },
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
                    Icon(Icons.Filled.ArrowBack, i18n("access.introduction.back.contact"), tint = Color.White)
                }
                Text(
                    text = i18n("introduction.title_second"),
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
                Column(Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp, viewModel.firstContact.value!!.authorId.bytes)
                    Text(viewModel.firstContact.value!!.displayName, Modifier.padding(top = 4.dp), Color.White, 16.sp)
                }
                Icon(Icons.Filled.SwapHoriz, i18n("access.swap"), modifier = Modifier.size(48.dp))
                Column(Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp, viewModel.secondContact.value!!.authorId.bytes)
                    Text(viewModel.secondContact.value!!.displayName, Modifier.padding(top = 4.dp), Color.White, 16.sp)
                }
            }
            Row(Modifier.padding(8.dp)) {
                TextField(
                    viewModel.introductionMessage.value,
                    viewModel::setIntroductionMessage,
                    placeholder = { Text(text = i18n("introduction.message")) },
                )
            }
            Row(Modifier.padding(8.dp)) {
                TextButton(
                    onClick = {
                        viewModel.makeIntroduction()
                        closeInfoDrawer()
                    },
                    Modifier.fillMaxWidth()
                ) {
                    val text = i18n("introduction.introduce")
                    Text(text.uppercase(Locale.getDefault()))
                }
            }
        }
    }
}
