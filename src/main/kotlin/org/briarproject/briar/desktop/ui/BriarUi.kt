package org.briarproject.briar.desktop.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.briar.desktop.login.LoginScreen
import org.briarproject.briar.desktop.login.RegistrationScreen
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.ViewModelProvider
import java.awt.Dimension
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

enum class Screen {
    REGISTRATION,
    LOGIN,
    MAIN
}

interface BriarUi {

    @Composable
    fun start(onClose: () -> Unit)

    fun stop()
}

val LocalViewModelProvider = staticCompositionLocalOf<ViewModelProvider?> { null }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val accountManager: AccountManager,
    private val lifecycleManager: LifecycleManager,
    private val viewModelProvider: ViewModelProvider,
) : BriarUi {

    override fun stop() {
        // TODO: check how briar is doing this
        if (lifecycleManager.lifecycleState == RUNNING) {
            lifecycleManager.stopServices()
            lifecycleManager.waitForShutdown()
        }
    }

    @Composable
    override fun start(onClose: () -> Unit) {
        val (isDark, setDark) = remember { mutableStateOf(true) }
        val title = i18n("main.title")
        var screenState by remember {
            mutableStateOf(
                if (accountManager.hasDatabaseKey()) {
                    // this should only happen during testing when we launch the main UI directly
                    // without a need to enter the password.
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
            onCloseRequest = onClose,
            icon = painterResource("images/logo_circle.svg")
        ) {
            window.minimumSize = Dimension(800, 600)
            CompositionLocalProvider(LocalViewModelProvider provides viewModelProvider) {
                BriarTheme(isDarkTheme = isDark) {
                    when (screenState) {
                        Screen.REGISTRATION ->
                            RegistrationScreen(
                                onSignedUp = {
                                    screenState = Screen.MAIN
                                }
                            )
                        Screen.LOGIN ->
                            LoginScreen(
                                onSignedIn = {
                                    screenState = Screen.MAIN
                                }
                            )
                        else ->
                            MainScreen(isDark, setDark)
                    }
                }
            }
        }
    }
}
