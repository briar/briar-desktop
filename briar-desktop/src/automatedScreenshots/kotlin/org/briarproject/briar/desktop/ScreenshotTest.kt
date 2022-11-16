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

package org.briarproject.briar.desktop

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.window.FrameWindowScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.plugin.LanTcpConstants
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.ui.LocalWindowFocusState
import org.briarproject.briar.desktop.ui.LocalWindowScope
import org.briarproject.briar.desktop.ui.WindowFocusState
import org.jetbrains.annotations.NonNls
import org.jetbrains.skia.Image
import org.junit.Test
import java.io.FileOutputStream

@OptIn(ExperimentalTestApi::class)
class ScreenshotTest {
    @Test
    fun makeScreenshot() = runDesktopComposeUiTest(700, 700) {
        // TODO: unify with interactive tests
        val dataDir = getDataDir()
        val app =
            DaggerBriarDesktopTestApp.builder().desktopCoreModule(
                DesktopCoreModule(dataDir, DEFAULT_SOCKS_PORT, DEFAULT_CONTROL_PORT)
            ).build()
        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        windowScope = object : FrameWindowScope {
            override val window: ComposeWindow get() = TODO()
        }
        windowFocusState = WindowFocusState().apply { focused = true }

        setContent {
            CompositionLocalProvider(
                LocalWindowScope provides windowScope,
                LocalWindowFocusState provides windowFocusState,
            ) {
                app.getBriarUi().content()
            }
        }

        captureToImage().save("before-click.png")
        onNodeWithTag("close_expiration").performClick()
        captureToImage().save("after-click.png")

        // TODO: unify with interactive tests
        val lifecycleManager = app.getLifecycleManager()
        val accountManager = app.getAccountManager()

        @NonNls
        val password = "verySecret123!"
        accountManager.createAccount("alice", password)

        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()

        runBlocking {
            delay(1000)

            captureToImage().save("after-login.png")

            app.getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
            app.getContactManager().addPendingContact("briar://aatkjq4seoualafpwh4cfckdzr4vpr4slk3bbvpxklf7y7lv4ajw6", "Faythe")

            app.getEventBus().addListener { e ->
                if (e is ContactAddedEvent) {
                    if (app.getContactManager().getContact(e.contactId).author.name in listOf("Bob", "Chuck")) // NON-NLS
                        app.getIoExecutor().execute {
                            app.getConnectionRegistry().registerIncomingConnection(e.contactId, LanTcpConstants.ID) {}
                        }
                }
            }

            delay(1000)

            val viewModel = app.getViewModelProvider().get(ContactListViewModel::class)
            viewModel.selectContact(viewModel.contactList.value[1])

            delay(1000)

            captureToImage().save("after-contacts.png")
        }
    }
}

private fun Image.save(file: String) {
    encodeToData()?.bytes?.let { bytes ->
        FileOutputStream(file).use { out ->
            out.write(bytes)
        }
    }
}
