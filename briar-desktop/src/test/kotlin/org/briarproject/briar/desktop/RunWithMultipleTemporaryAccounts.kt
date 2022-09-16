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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.application
import mu.KotlinLogging
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.briarproject.briar.desktop.settings.DesktopSettingsModule
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import org.briarproject.briar.desktop.utils.LogUtils
import org.jetbrains.annotations.NonNls
import java.util.logging.Level.ALL

internal class RunWithMultipleTemporaryAccounts(
    private val names: List<String>,
    val customization: List<BriarDesktopTestApp>.() -> Unit,
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val apps = mutableListOf<BriarDesktopTestApp>()

    fun run() {
        LogUtils.setupLogging(ALL)

        for (i in names.indices) {
            val name = names[i]
            val app = app(name, DEFAULT_SOCKS_PORT + 2 * i, DEFAULT_CONTROL_PORT + 2 * i)
            apps.add(app)
        }

        application {
            LaunchedEffect(Unit) {
                apps.forEach {
                    val accountManager = it.getAccountManager()
                    val lifecycleManager = it.getLifecycleManager()
                    val dbKey = accountManager.databaseKey ?: throw AssertionError()
                    lifecycleManager.startServices(dbKey)
                    lifecycleManager.waitForStartup()
                }

                customization(apps)
            }

            for (app in apps) {
                start(app, this)
            }
        }
    }

    private fun app(name: String, socksPort: Int, controlPort: Int): BriarDesktopTestApp {
        val dataDir = getDataDir()
        LOG.i { "Using data directory '$dataDir'" }

        val app =
            DaggerBriarDesktopTestApp.builder()
                .desktopCoreModule(DesktopCoreModule(dataDir, socksPort, controlPort))
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

        val accountManager = app.getAccountManager()

        @NonNls
        val password = "verySecret123!"
        accountManager.createAccount(name, password)

        return app
    }

    @Composable
    fun start(app: BriarDesktopTestApp, applicationScope: ApplicationScope) {
        app.getBriarUi().start {
            apps.forEach {
                it.getBriarUi().stop()
            }
            applicationScope.exitApplication()
        }
    }
}
