package org.briarproject.briar.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.introduction.IntroductionManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import java.util.logging.Logger.getLogger
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
@Singleton
internal class UI
@Inject
constructor(
    private val briarService: BriarService,
    private val accountManager: AccountManager,
    private val contactManager: ContactManager,
    private val messagingManager: MessagingManager,
    private val introductionManager: IntroductionManager,
    private val conversationManager: ConversationManager,
    private val privateMessageFactory: PrivateMessageFactory,
    private val eventBus: EventBus,
    private val passwordStrengthEstimator: PasswordStrengthEstimator
) {

    private val logger = getLogger(UI::javaClass.name)
//    private val configuration = Configuration()

    internal fun startBriar() {
        briarService.start();
    }

    internal fun startUI() {
        Window {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TheImage()
                Spacer(Modifier.height(32.dp))
                TheText()
                TheButton()
            }
        }
    }

    internal fun getContactManager(): ContactManager {
        return contactManager
    }

}


@Composable
private fun TheButton() {
    var text by remember { mutableStateOf("Start chatting") }
    Button(onClick = {
        text = "Sorry, not yet available"
    }) {
        Text(text)
    }
}

@Composable
private fun TheImage() {
    Image(
        painter = svgResource("images/logo_circle.svg"),
        contentDescription = "Briar logo",
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(400.dp))
    )
}

@Composable
private fun TheText() {
    Text("Welcome to Briar")
}
