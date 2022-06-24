/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.Strings
import org.briarproject.briar.desktop.attachment.media.AvatarManager
import org.briarproject.briar.desktop.expiration.ExpirationBanner
import org.briarproject.briar.desktop.login.ErrorScreen
import org.briarproject.briar.desktop.login.StartupScreen
import org.briarproject.briar.desktop.notification.NotificationProvider
import org.briarproject.briar.desktop.notification.SoundNotificationProvider
import org.briarproject.briar.desktop.notification.VisualNotificationProvider
import org.briarproject.briar.desktop.settings.Configuration
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme.AUTO
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme.DARK
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.MessageCounterDataType.Forum
import org.briarproject.briar.desktop.ui.MessageCounterDataType.PrivateMessage
import org.briarproject.briar.desktop.ui.Screen.EXPIRED
import org.briarproject.briar.desktop.ui.Screen.MAIN
import org.briarproject.briar.desktop.ui.Screen.STARTUP
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.UiUtils.DensityDimension
import org.briarproject.briar.desktop.utils.UiUtils.GlobalDensity
import org.briarproject.briar.desktop.viewmodel.ViewModelProvider
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

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
val LocalAvatarManager = staticCompositionLocalOf<AvatarManager?> { null }
val LocalConfiguration = staticCompositionLocalOf<Configuration?> { null }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val lifecycleManager: LifecycleManager,
    private val eventBus: EventBus,
    private val viewModelProvider: ViewModelProvider,
    private val avatarManager: AvatarManager,
    private val configuration: Configuration,
    private val visualNotificationProvider: VisualNotificationProvider,
    private val soundNotificationProvider: SoundNotificationProvider,
    private val messageCounter: MessageCounterImpl,
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun start(onClose: () -> Unit) {
        val focusState = remember { WindowFocusState() }

        Window(
            title = Strings.APP_NAME,
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

                val notificationCoolDown = 5.seconds.inWholeMilliseconds
                var lastNotificationPrivateMessage = 0L
                var lastNotificationForum = 0L

                val eventListener = EventListener { e ->
                    when (e) {
                        is LifecycleEvent ->
                            if (e.lifecycleState == RUNNING) screenState = MAIN
                    }
                }
                val focusListener = object : WindowFocusListener {
                    override fun windowGainedFocus(e: WindowEvent?) {
                        focusState.focused = true
                        window.iconImage = iconNormal
                    }

                    override fun windowLostFocus(e: WindowEvent?) {
                        focusState.focused = false
                        // reset notification cool-down
                        lastNotificationPrivateMessage = 0
                        lastNotificationForum = 0
                    }
                }
                val messageCounterListener: MessageCounterListener = { (type, total, groups, inc) ->
                    if (inc && total > 0 && !focusState.focused) {
                        val callback: NotificationProvider.() -> Unit = when (type) {
                            PrivateMessage -> {
                                { notifyPrivateMessages(total, groups) }
                            }

                            Forum -> {
                                { notifyForumPosts(total, groups) }
                            }
                        }
                        val (lastNotification, setLastNotification) = when (type) {
                            PrivateMessage -> lastNotificationPrivateMessage to { v: Long ->
                                lastNotificationPrivateMessage = v
                            }

                            Forum -> lastNotificationForum to { v: Long -> lastNotificationForum = v }
                        }

                        window.iconImage = iconBadge
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastNotification > notificationCoolDown) {
                            if (configuration.visualNotifications)
                                visualNotificationProvider.apply(callback)
                            if (configuration.soundNotifications)
                                soundNotificationProvider.apply(callback)
                            setLastNotification(currentTime)
                        }
                    }
                }

                visualNotificationProvider.init()
                soundNotificationProvider.init()
                eventBus.addListener(eventListener)
                window.addWindowFocusListener(focusListener)
                messageCounter.addListener(messageCounterListener)

                onDispose {
                    messageCounter.removeListener(messageCounterListener)
                    eventBus.removeListener(eventListener)
                    window.removeWindowFocusListener(focusListener)
                    visualNotificationProvider.uninit()
                    soundNotificationProvider.uninit()
                }
            }

            CompositionLocalProvider(
                LocalDensity provides Density(configuration.uiScale ?: GlobalDensity),
            ) {
                window.minimumSize = DensityDimension(800, 600)
                window.preferredSize = DensityDimension(800, 600)
            }

            CompositionLocalProvider(
                LocalWindowScope provides this,
                LocalWindowFocusState provides focusState,
                LocalViewModelProvider provides viewModelProvider,
                LocalAvatarManager provides avatarManager,
                LocalConfiguration provides configuration,
                LocalTextContextMenu provides BriarTextContextMenu,
            ) {
                // invalidate whole application window in case the theme or language setting is changed
                configuration.invalidateScreen.react {
                    return@CompositionLocalProvider
                }

                val isDarkTheme = configuration.theme == DARK ||
                    (configuration.theme == AUTO && isSystemInDarkTheme())
                BriarTheme(isDarkTheme, configuration.uiScale) {
                    Column(Modifier.fillMaxSize()) {
                        ExpirationBanner { screenState = EXPIRED; stop() }
                        when (screenState) {
                            STARTUP -> StartupScreen()
                            MAIN -> MainScreen()
                            EXPIRED -> ErrorScreen(i18n("startup.failed.expired"))
                        }
                    }
                }
            }
        }
    }
}
