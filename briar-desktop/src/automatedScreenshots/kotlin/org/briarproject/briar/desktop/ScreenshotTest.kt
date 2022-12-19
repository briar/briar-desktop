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

// TODO: Remove `invisible_reference` and `invisible_member`
//  when https://youtrack.jetbrains.com/issue/KTIJ-23114/ is fixed.
@file:Suppress("invisible_reference", "invisible_member", "nls")

package org.briarproject.briar.desktop

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.window.FrameWindowScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils.deleteDirectory
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.plugin.LanTcpConstants
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

    private fun DesktopComposeUiTest.start(): BriarDesktopTestApp {
        // TODO: unify with interactive tests
        val dataDir = getDataDir()
        val app =
            DaggerBriarDesktopTestApp.builder().desktopCoreModule(
                DesktopCoreModule(dataDir)
            ).build()

        app.getShutdownManager().addShutdownHook {
            deleteDirectory(dataDir.toFile())
        }

        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        val windowScope = object : FrameWindowScope {
            // would be needed for launching dialogs like the image picker
            override val window: ComposeWindow get() = TODO()
        }
        val windowFocusState = WindowFocusState().apply { focused = true }

        setContent {
            CompositionLocalProvider(
                LocalWindowScope provides windowScope,
                LocalWindowFocusState provides windowFocusState,
            ) {
                app.getBriarUi().content()
            }
        }

        return app
    }

    private fun BriarDesktopTestApp.login() {
        // TODO: unify with interactive tests
        val lifecycleManager = getLifecycleManager()
        val accountManager = getAccountManager()

        @NonNls
        val password = "verySecret123!"
        accountManager.createAccount("alice", password)

        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }

    @Test
    fun makeScreenshot() = runDesktopComposeUiTest(700, 700) {
        runBlocking {
            val app = start()

            // close expiration banner and move mouse outside of window (to avoid hover effect)
            // TODO: *sometimes* leads to Compose crash (somehow connected to Tooltip?)
            onNodeWithTag("close_expiration").performClick()
            onRoot().performMouseInput { moveTo(Offset(-10f, -10f)) }

            with(app) {
                login()
                awaitIdle()

                getEventBus().addListener { e ->
                    if (e is ContactAddedEvent)
                        if (getContactManager().getContact(e.contactId).author.name in listOf("Bob", "Chuck"))
                            getIoExecutor().execute {
                                getConnectionRegistry().registerIncomingConnection(e.contactId, LanTcpConstants.ID) {}
                            }
                }

                getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
                getContactManager().addPendingContact(
                    "briar://aatkjq4seoualafpwh4cfckdzr4vpr4slk3bbvpxklf7y7lv4ajw6",
                    "Faythe"
                )

                val viewModel = getViewModelProvider().get(ContactListViewModel::class)

                // give IO executor some time to add contacts and messages
                delay(10_000)
                waitUntil(60_000) { viewModel.combinedContactList.value.size > 2 }

                // select Bob in list of contacts and wait for the chat history to load
                viewModel.selectContact(viewModel.combinedContactList.value[1])
                awaitIdle()

                captureToImage().save("contact-list.png")
            }
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
