package org.briarproject.briar.desktop.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.login.Login
import org.briarproject.briar.desktop.login.LoginViewModel
import org.briarproject.briar.desktop.login.Registration
import org.briarproject.briar.desktop.login.RegistrationViewModel
import org.briarproject.briar.desktop.theme.BriarTheme
import java.awt.Dimension
import java.util.logging.Logger
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

enum class Screen {
    REGISTRATION,
    LOGIN,
    MAIN
}

interface BriarUi {

    fun start()

    fun stop()
}

val CVM = compositionLocalOf<ConversationManager> { error("Undefined ConversationManager") }
val CTM = compositionLocalOf<ContactManager> { error("Undefined ContactManager") }
val MM = compositionLocalOf<MessagingManager> { error("Undefined MessagingManager") }
val IM = compositionLocalOf<IdentityManager> { error("Undefined IdentityManager") }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val registrationViewModel: RegistrationViewModel,
    private val loginViewModel: LoginViewModel,
    private val contactListViewModel: ContactListViewModel,
    private val addContactViewModel: AddContactViewModel,
    private val introductionViewModel: IntroductionViewModel,
    private val accountManager: AccountManager,
    private val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val identityManager: IdentityManager,
    private val messagingManager: MessagingManager,
    private val lifecycleManager: LifecycleManager,
) : BriarUi {

    companion object {
        private val LOG = Logger.getLogger(BriarUiImpl::class.java.name)
    }

    override fun stop() {
        // TODO: check how briar is doing this
        if (lifecycleManager.lifecycleState == RUNNING) {
            lifecycleManager.stopServices()
            lifecycleManager.waitForShutdown()
        }
    }

    override fun start() {
        application {
            val (isDark, setDark) = remember { mutableStateOf(true) }
            val title = "Briar Desktop"
            var screenState by remember {
                mutableStateOf(
                    if (accountManager.hasDatabaseKey()) {
                        // this should only happen during testing when we launch the main UI directly
                        // without a need to enter the password.
                        contactListViewModel.loadContacts()
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
                onCloseRequest = { stop(); exitApplication() },
            ) {
                window.minimumSize = Dimension(800, 600)
                BriarTheme(isDarkTheme = isDark) {
                    when (screenState) {
                        Screen.REGISTRATION ->
                            Registration(registrationViewModel) {
                                screenState = Screen.MAIN
                            }
                        Screen.LOGIN ->
                            Login(loginViewModel) {
                                contactListViewModel.loadContacts()
                                screenState = Screen.MAIN
                            }
                        else ->
                            CompositionLocalProvider(
                                CVM provides conversationManager,
                                CTM provides contactManager,
                                MM provides messagingManager,
                                IM provides identityManager,
                            ) {
                                MainScreen(
                                    contactListViewModel,
                                    addContactViewModel,
                                    introductionViewModel,
                                    isDark,
                                    setDark
                                )
                            }
                    }
                }
            }
        }
    }
}
