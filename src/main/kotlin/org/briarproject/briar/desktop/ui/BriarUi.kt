/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocalization
import androidx.compose.ui.platform.PlatformLocalization
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.FeatureFlags
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.DesktopFeatureFlags
import org.briarproject.briar.desktop.expiration.ExpirationBanner
import org.briarproject.briar.desktop.login.ErrorScreen
import org.briarproject.briar.desktop.login.StartupScreen
import org.briarproject.briar.desktop.settings.Settings
import org.briarproject.briar.desktop.settings.Settings.Theme.AUTO
import org.briarproject.briar.desktop.settings.Settings.Theme.DARK
import org.briarproject.briar.desktop.settings.SettingsViewModel
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.Screen.EXPIRED
import org.briarproject.briar.desktop.ui.Screen.MAIN
import org.briarproject.briar.desktop.ui.Screen.STARTUP
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.ViewModelProvider
import org.briarproject.briar.desktop.viewmodel.viewModel
import java.awt.Dimension
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

enum class Screen {
    STARTUP,
    MAIN,
    EXPIRED,
}

interface BriarUi {

    @Composable
    fun start(onClose: () -> Unit)

    fun stop()
}

val LocalWindowScope = staticCompositionLocalOf<FrameWindowScope?> { null }
val LocalViewModelProvider = staticCompositionLocalOf<ViewModelProvider?> { null }
val LocalCoreFeatureFlags = staticCompositionLocalOf<FeatureFlags?> { null }
val LocalDesktopFeatureFlags = staticCompositionLocalOf<DesktopFeatureFlags?> { null }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val lifecycleManager: LifecycleManager,
    private val eventBus: EventBus,
    private val viewModelProvider: ViewModelProvider,
    private val settings: Settings,
    private val featureFlags: FeatureFlags,
    private val desktopFeatureFlags: DesktopFeatureFlags,
) : BriarUi, EventListener {

    private var screenState by mutableStateOf(
        if (lifecycleManager.lifecycleState == RUNNING) MAIN
        else STARTUP
    )

    override fun eventOccurred(e: Event?) {
        if (e is LifecycleEvent && e.lifecycleState == RUNNING)
            screenState = MAIN
    }

    override fun stop() {
        if (lifecycleManager.lifecycleState == RUNNING) {
            lifecycleManager.stopServices()
            lifecycleManager.waitForShutdown()
        }
        eventBus.removeListener(this)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun start(onClose: () -> Unit) {
        // invalidate whole application window in case the theme or language setting is changed
        settings.invalidateScreen.react {}

        val title = i18n("main.title")
        val platformLocalization = object : PlatformLocalization {
            override val copy = i18n("copy")
            override val cut = i18n("cut")
            override val paste = i18n("paste")
            override val selectAll = i18n("select_all")
        }
        eventBus.addListener(this)
        Window(
            title = title,
            onCloseRequest = onClose,
            icon = painterResource("images/logo_circle.svg") // NON-NLS
        ) {
            window.minimumSize = Dimension(800, 600)
            CompositionLocalProvider(
                LocalWindowScope provides this,
                LocalViewModelProvider provides viewModelProvider,
                LocalCoreFeatureFlags provides featureFlags,
                LocalDesktopFeatureFlags provides desktopFeatureFlags,
                LocalLocalization provides platformLocalization,
            ) {
                var showAbout by remember { mutableStateOf(false) }
                val settingsViewModel: SettingsViewModel = viewModel()
                val isDarkTheme = settings.theme == DARK ||
                    (settings.theme == AUTO && isSystemInDarkTheme())
                BriarTheme(isDarkTheme) {
                    Column(Modifier.fillMaxSize()) {
                        ExpirationBanner { screenState = EXPIRED; stop() }
                        when (screenState) {
                            STARTUP -> StartupScreen(onShowAbout = { showAbout = true })
                            MAIN -> MainScreen(settingsViewModel, showAbout = { showAbout = true })
                            EXPIRED -> ErrorScreen(i18n("startup.failed.expired"), onShowAbout = { showAbout = true })
                        }
                    }
                    if (showAbout) {
                        AboutDialog(onClose = { showAbout = false })
                    }
                }
            }
        }
    }
}
