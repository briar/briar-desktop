package org.briarproject.briar.desktop.conversation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.contact.ContactInfoDrawer
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.navigation.SIDEBAR_WIDTH
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.CONTACTLIST_WIDTH

@Composable
fun Conversation(
    contact: Contact,
    introductionViewModel: IntroductionViewModel,
) {
    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(ContactInfoDrawerState.MakeIntro) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val animatedInfoDrawerOffsetX by animateDpAsState(if (infoDrawer) (-275).dp else 0.dp)
        Scaffold(
            topBar = {
                ConversationHeader(
                    contact,
                    onMakeIntroduction = {
                        introductionViewModel.apply {
                            setFirstContact(contact)
                            loadContacts()
                        }
                        setInfoDrawer(true)
                    }
                )
            },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    val chat = ChatState(contact.id)
                    TextBubbles(chat.value)
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
                ContactInfoDrawer(introductionViewModel, setInfoDrawer, contactDrawerState)
            }
        }
    }
}
