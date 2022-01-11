package org.briarproject.briar.desktop.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.FeatureFlags
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.DesktopFeatureFlags
import org.briarproject.briar.desktop.login.AccountDeletedEvent
import org.briarproject.briar.desktop.login.ErrorScreen
import org.briarproject.briar.desktop.login.LoginScreen
import org.briarproject.briar.desktop.login.RegistrationScreen
import org.briarproject.briar.desktop.login.StartupFailedEvent
import org.briarproject.briar.desktop.settings.SettingsViewModel
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.Screen.LOGIN
import org.briarproject.briar.desktop.ui.Screen.MAIN
import org.briarproject.briar.desktop.ui.Screen.REGISTRATION
import org.briarproject.briar.desktop.ui.Screen.STARTUP_ERROR
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.ViewModelProvider
import org.briarproject.briar.desktop.viewmodel.viewModel
import java.awt.Dimension
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

sealed interface Screen {
    object REGISTRATION : Screen
    object LOGIN : Screen
    object MAIN : Screen
    class STARTUP_ERROR(val error: StartResult) : Screen
}

interface BriarUi {

    @Composable
    fun start(onClose: () -> Unit)

    fun stop()
}

val LocalViewModelProvider = staticCompositionLocalOf<ViewModelProvider?> { null }
val LocalCoreFeatureFlags = staticCompositionLocalOf<FeatureFlags?> { null }
val LocalDesktopFeatureFlags = staticCompositionLocalOf<DesktopFeatureFlags?> { null }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val accountManager: AccountManager,
    private val lifecycleManager: LifecycleManager,
    private val eventBus: EventBus,
    private val viewModelProvider: ViewModelProvider,
    private val featureFlags: FeatureFlags,
    private val desktopFeatureFlags: DesktopFeatureFlags,
) : BriarUi, EventListener {

    private var screenState by mutableStateOf(
        if (lifecycleManager.lifecycleState == RUNNING) MAIN
        else if (accountManager.accountExists()) LOGIN
        else REGISTRATION
    )

    override fun eventOccurred(e: Event?) {
        when {
            e is LifecycleEvent && e.lifecycleState == RUNNING ->
                screenState = MAIN
            e is AccountDeletedEvent ->
                screenState = REGISTRATION
            e is StartupFailedEvent ->
                screenState = STARTUP_ERROR(e.result)
        }
    }

    override fun stop() {
        // TODO: check how briar is doing this
        if (lifecycleManager.lifecycleState == RUNNING) {
            lifecycleManager.stopServices()
            lifecycleManager.waitForShutdown()
        }
        eventBus.removeListener(this)
    }

    @Composable
    override fun start(onClose: () -> Unit) {
        val title = i18n("main.title")
        eventBus.addListener(this)
        Window(
            title = title,
            onCloseRequest = onClose,
            icon = painterResource("images/logo_circle.svg")
        ) {
            window.minimumSize = Dimension(800, 600)
            CompositionLocalProvider(
                LocalViewModelProvider provides viewModelProvider,
                LocalCoreFeatureFlags provides featureFlags,
                LocalDesktopFeatureFlags provides desktopFeatureFlags
            ) {
                val settingsViewModel: SettingsViewModel = viewModel()
                BriarTheme(isDarkTheme = settingsViewModel.isDarkMode.value) {
                    when (val state = screenState) {
                        is REGISTRATION -> RegistrationScreen()
                        is LOGIN -> LoginScreen()
                        is STARTUP_ERROR -> ErrorScreen(state.error)
                        is MAIN -> MainScreen(settingsViewModel)
                    }
                }
            }
        }
    }
}
