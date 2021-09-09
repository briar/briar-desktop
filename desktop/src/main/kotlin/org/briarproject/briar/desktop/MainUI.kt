package org.briarproject.briar.desktop

import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.introduction.IntroductionManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageFactory

class MainUI(
    private val briarService: BriarService,
    val accountManager: AccountManager,
    val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val messagingManager: MessagingManager,
    private val introductionManager: IntroductionManager,
    private val privateMessageFactory: PrivateMessageFactory,
    private val eventBus: EventBus,
    val passwordStrengthEstimator: PasswordStrengthEstimator
) {

    init {
        // Should be shown only when logged in
//        val title = "Briar Desktop"
//        Window (title = title) {
//            Column(
//                modifier = Modifier.padding(16.dp).fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Welcome to Briar")
//            }
//        }
    }
}
