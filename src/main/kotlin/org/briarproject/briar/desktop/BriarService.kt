package org.briarproject.briar.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.desktop.dialogs.Login
import org.briarproject.briar.desktop.dialogs.Registration
import org.briarproject.briar.desktop.paul.theme.BriarTheme
import org.briarproject.briar.desktop.paul.views.BriarUIStateManager
import java.awt.Dimension
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess

enum class Screen {
    REGISTRATION,
    LOGIN,
    MAIN
}

interface BriarService {
    @Composable
    fun start(
        contactManager: ContactManager,
        conversationManager: ConversationManager,
        messagingManager: MessagingManager,
        identityManager: IdentityManager,
    )

    fun stop()
}

val CVM = compositionLocalOf<ConversationManager> { error("Undefined ConversationManager") }
val CTM = compositionLocalOf<ContactManager> { error("Undefined ContactManager") }
val MM = compositionLocalOf<MessagingManager> { error("Undefined MessagingManager") }
val IM = compositionLocalOf<IdentityManager> { error("Undefined IdentityManager") }

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

    override fun stop() {
        lifecycleManager.stopServices()
        lifecycleManager.waitForShutdown()
    }

    @Composable
    override fun start(
        contactManager: ContactManager,
        conversationManager: ConversationManager,
        messagingManager: MessagingManager,
        identityManager: IdentityManager,
    ) {
        val (isDark, setDark) = remember { mutableStateOf(true) }
        val title = "Briar Desktop"
        var screenState by remember {
            mutableStateOf(
                if (accountManager.hasDatabaseKey()) {
                    Screen.MAIN
                } else if (accountManager.accountExists()) {
                    Screen.LOGIN
                } else {
                    Screen.REGISTRATION
                }
            )
        }
        Window(
            title = title,
            onCloseRequest = { exitProcess(0) },
        ) {
            window.minimumSize = Dimension(800, 600)
            BriarTheme(isDarkTheme = isDark) {
                when (screenState) {
                    Screen.REGISTRATION ->
                        Registration(
                            onSubmit = { username, password ->
                                accountManager.createAccount(username, password)
                                signedIn()
                                screenState = Screen.MAIN
                            }
                        )
                    Screen.LOGIN ->
                        Login(
                            onResult = {
                                try {
                                    accountManager.signIn(it)
                                    signedIn()
                                    screenState = Screen.MAIN
                                } catch (e: DecryptionException) {
                                    // failure, try again
                                }
                            }
                        )

                    else ->
                        CompositionLocalProvider(
                            CVM provides conversationManager,
                            CTM provides contactManager,
                            MM provides messagingManager,
                            IM provides identityManager,
                        ) {
                            BriarUIStateManager(contacts, isDark, setDark)
                        }
                }
            }
        }
    }

    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
        val contacts = contactManager.contacts
        for (contact in contacts) {
            println("${contact.author.name} (${contact.alias})")
            this.contacts.add(contact)
        }
    }
}
