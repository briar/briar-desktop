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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.LogUtils
import org.jetbrains.annotations.NonNls
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.logging.Level.ALL

internal class RunWithTemporaryAccount(
    val createAccount: Boolean = true,
    val login: Boolean = true,
    val makeDirUnwritable: Boolean = false,
    val customization: BriarDesktopTestApp.() -> Unit = {}
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() {
        LogUtils.setupLogging(ALL)

        val dataDir = getDataDir()
        LOG.i { "Using data directory '$dataDir'" }

        if (makeDirUnwritable) {
            @NonNls
            val permissions = PosixFilePermissions.fromString("r--r--r--")
            Files.setPosixFilePermissions(dataDir, permissions)
        }

        val app =
            DaggerBriarDesktopTestApp.builder().desktopTestModule(
                DesktopTestModule(dataDir)
            ).build()

        app.getShutdownManager().addShutdownHook {
            LOG.i { "deleting temporary account at $dataDir" }
            org.apache.commons.io.FileUtils.deleteDirectory(dataDir.toFile())
        }

        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        val lifecycleManager = app.getLifecycleManager()
        val accountManager = app.getAccountManager()

        if (createAccount) {
            @NonNls
            val password = "verySecret123!"
            accountManager.createAccount("alice", password)
        }

        application {
            LaunchedEffect(Unit) {
                delay(500)

                if (createAccount && login) {
                    val dbKey = accountManager.databaseKey ?: throw AssertionError()
                    lifecycleManager.startServices(dbKey)
                    lifecycleManager.waitForStartup()

                    customization(app)
                }
            }

            app.getBriarUi().start {
                app.getBriarUi().stop()
                exitApplication()
            }
        }
    }
}
