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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLocalization
import androidx.compose.ui.platform.PlatformLocalization
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.desktop.conversation.ConversationMessagesReadEvent
import org.briarproject.briar.desktop.expiration.ExpirationBanner
import org.briarproject.briar.desktop.login.ErrorScreen
import org.briarproject.briar.desktop.login.StartupScreen
import org.briarproject.briar.desktop.notification.NotificationProvider
import org.briarproject.briar.desktop.settings.Configuration
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme.AUTO
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme.DARK
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.Screen.EXPIRED
import org.briarproject.briar.desktop.ui.Screen.MAIN
import org.briarproject.briar.desktop.ui.Screen.STARTUP
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.ViewModelProvider
import java.awt.Dimension
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
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
val LocalConfiguration = staticCompositionLocalOf<Configuration?> { null }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val lifecycleManager: LifecycleManager,
    private val eventBus: EventBus,
    private val viewModelProvider: ViewModelProvider,
    private val configuration: Configuration,
    private val notificationProvider: NotificationProvider,
) : BriarUi {

    private var screenState by mutableStateOf(
        if (lifecycleManager.lifecycleState == RUNNING) MAIN
        else STARTUP
    )

    override fun stop() {
        if (lifecycleManager.lifecycleState == RUNNING) {
            lifecycleManager.stopServices()
            lifecycleManager.waitForShutdown()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun start(onClose: () -> Unit) {
        val title = i18n("main.title")
        val platformLocalization = object : PlatformLocalization {
            override val copy = i18n("copy")
            override val cut = i18n("cut")
            override val paste = i18n("paste")
            override val selectAll = i18n("select_all")
        }
        val focusState = remember { WindowFocusState() }

        Window(
            title = title,
            onCloseRequest = onClose,
        ) {
            // changing the icon in the Composable itself automatically brings the window to front
            // see https://github.com/JetBrains/compose-jb/issues/1861
            // therefore the icon is set here on the AWT Window
            val iconNormal = painterResource("images/logo_circle.svg") // NON-NLS
                .toAwtImage(LocalDensity.current, LocalLayoutDirection.current, Size(32f, 32f))
            val iconBadge = painterResource("images/logo_circle_badge.svg") // NON-NLS
                .toAwtImage(LocalDensity.current, LocalLayoutDirection.current, Size(32f, 32f))

            DisposableEffect(Unit) {
                // todo: hard-coded messageCount doesn't account for unread messages on application start
                // also see https://code.briarproject.org/briar/briar-desktop/-/issues/133
                var messageCount = 0

                val eventListener = EventListener { e ->
                    when (e) {
                        is LifecycleEvent ->
                            if (e.lifecycleState == RUNNING) screenState = MAIN
                        is ConversationMessageReceivedEvent<*> -> {
                            messageCount++
                            if (!focusState.focused) {
                                window.iconImage = iconBadge
                                notificationProvider.notifyPrivateMessages(messageCount)
                            }
                        }
                        is ConversationMessagesReadEvent -> {
                            messageCount -= e.count
                            if (messageCount < 0) messageCount = 0
                        }
                    }
                }
                val focusListener = object : WindowFocusListener {
                    override fun windowGainedFocus(e: WindowEvent?) {
                        focusState.focused = true
                        window.iconImage = iconNormal
                    }

                    override fun windowLostFocus(e: WindowEvent?) {
                        focusState.focused = false
                    }
                }

                notificationProvider.init()
                eventBus.addListener(eventListener)
                window.addWindowFocusListener(focusListener)

                onDispose {
                    eventBus.removeListener(eventListener)
                    window.removeWindowFocusListener(focusListener)
                    notificationProvider.uninit()
                }
            }

            window.minimumSize = Dimension(800, 600)
            CompositionLocalProvider(
                LocalWindowScope provides this,
                LocalWindowFocusState provides focusState,
                LocalViewModelProvider provides viewModelProvider,
                LocalConfiguration provides configuration,
                LocalLocalization provides platformLocalization,
            ) {
                // invalidate whole application window in case the theme or language setting is changed
                configuration.invalidateScreen.react {
                    window.title = i18n("main.title")
                    return@CompositionLocalProvider
                }

                var showAbout by remember { mutableStateOf(false) }
                val isDarkTheme = configuration.theme == DARK ||
                    (configuration.theme == AUTO && isSystemInDarkTheme())
                BriarTheme(isDarkTheme) {
                    Column(Modifier.fillMaxSize()) {
                        ExpirationBanner { screenState = EXPIRED; stop() }
                        when (screenState) {
                            STARTUP -> StartupScreen(onShowAbout = { showAbout = true })
                            MAIN -> MainScreen(onShowAbout = { showAbout = true })
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
