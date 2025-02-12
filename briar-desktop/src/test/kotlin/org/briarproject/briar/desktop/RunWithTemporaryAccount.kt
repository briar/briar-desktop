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

@file:Suppress("HardCodedStringLiteral")

package org.briarproject.briar.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.application
import io.github.oshai.kotlinlogging.KotlinLogging
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.lifecycle.Service
import org.briarproject.bramble.api.lifecycle.ServiceException
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.briarproject.briar.desktop.settings.DesktopSettingsModule
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import org.briarproject.briar.desktop.utils.LogUtils
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.logging.Level.ALL

internal class RunWithTemporaryAccount(
    val createAccount: Boolean = true,
    val login: Boolean = true,
    val makeDirUnwritable: Boolean = false,
    val addBrokenService: Boolean = false,
    val customization: BriarDesktopTestApp.() -> Unit = {},
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    fun run() {
        LogUtils.setupLogging(ALL)

        val dataDir = getDataDir()
        LOG.i { "Using data directory '$dataDir'" }

        if (makeDirUnwritable) {
            val permissions = PosixFilePermissions.fromString("r--r--r--")
            Files.setPosixFilePermissions(dataDir, permissions)
        }

        val app =
            DaggerBriarDesktopTestApp.builder()
                .desktopCoreModule(DesktopCoreModule(dataDir))
                .desktopSettingsModule(DesktopSettingsModule("test"))
                .build()

        app.getShutdownManager().addShutdownHook {
            LOG.i { "deleting temporary account at $dataDir" }
            org.apache.commons.io.FileUtils.deleteDirectory(dataDir.toFile())
        }

        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            LOG.w(e) { "Uncaught exception in thread ${t.name}" }
        }

        val lifecycleManager = app.getLifecycleManager()
        val accountManager = app.getAccountManager()

        if (addBrokenService) {
            lifecycleManager.registerService(object : Service {
                override fun startService() {
                    throw ServiceException()
                }

                override fun stopService() {}
            })
        }

        if (createAccount) {
            val password = "verySecret123!"
            accountManager.createAccount("alice", password)
        }

        application {
            LaunchedEffect(Unit) {
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
