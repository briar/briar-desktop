package org.briarproject.briar.desktop

import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.desktop.dialogs.Login
import org.briarproject.briar.desktop.dialogs.Registration
import org.briarproject.briar.desktop.paul.theme.DarkColorPallet
import org.briarproject.briar.desktop.paul.theme.briarBlack
import org.briarproject.briar.desktop.paul.views.BriarUIStateManager
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

interface BriarService {
    @Composable
    fun start(
        conversationManager: ConversationManager,
        messagingManager: MessagingManager
    )

    fun stop()
}

val CM = compositionLocalOf<ConversationManager> { error("Undefined ConversationManager") }
val MM = compositionLocalOf<MessagingManager> { error("Undefined MessagingManager") }

@Immutable
@Singleton
internal class BriarServiceImpl
@Inject
constructor(
    private val accountManager: AccountManager,
    private val contactManager: ContactManager,
    private val messagingManager: MessagingManager,
    private val lifecycleManager: LifecycleManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator
) : BriarService {

    private val contacts: MutableList<Contact> = ArrayList()

    @Composable
    override fun start(
        conversationManager: ConversationManager,
        messagingManager: MessagingManager
    ) {
        if (!accountManager.accountExists()) {
            createAccount()
        } else {
            login(conversationManager, messagingManager)
        }
    }

    override fun stop() {
        lifecycleManager.stopServices()
        lifecycleManager.waitForShutdown()
    }

    private fun createAccount() {
        print("No account found. Let's create one!\n\n")
        Registration("Briar", accountManager, lifecycleManager)
    }

    @Composable
    private fun login(
        conversationManager: ConversationManager,
        messagingManager: MessagingManager
    ) {
        val title = "Briar Desktop"
        var screenState by remember { mutableStateOf<Screen>(Screen.Login) }
        Window(title = title) {
            MaterialTheme(colors = DarkColorPallet) {
                when (val screen = screenState) {
                    is Screen.Login ->
                        Login(
                            "Briar",
                            modifier = Modifier.background(MaterialTheme.colors.background),
                            onResult = {
                                try {
                                    accountManager.signIn(it)
                                    signedIn()
                                    screenState = Screen.Main
                                } catch (e: DecryptionException) {
                                    // failure, try again
                                }
                            }
                        )

                    is Screen.Main ->
                        CompositionLocalProvider(
                            CM provides conversationManager,
                            MM provides messagingManager
                        ) {
                            BriarUIStateManager(contacts)
                        }
                }
            }
        }
    }

    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
        val contacts: Collection<Contact> = contactManager.getContacts()
        for (contact in contacts) {
            println("${contact.author.name} (${contact.alias})")
            this.contacts.add(contact)
        }
    }
}
