package org.briarproject.briar.desktop

import androidx.compose.runtime.Composable
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

    @Composable
    internal fun startBriar() {
        briarService.start();
    }

    internal fun getContactManager(): ContactManager {
        return contactManager
    }

}