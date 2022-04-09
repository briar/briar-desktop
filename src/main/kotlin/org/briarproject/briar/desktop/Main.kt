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

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import mu.KotlinLogging
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.utils.FileUtils
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.LogUtils
import org.jetbrains.annotations.NonNls
import java.io.File.separator
import java.io.IOException
import java.lang.System.getProperty
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Level.ALL
import java.util.logging.Level.INFO
import java.util.logging.Level.WARNING

@NonNls
private val DEFAULT_DATA_DIR =
    getProperty("user.home") + separator + ".briar" + separator + "desktop"

private class Main : CliktCommand(
    name = "briar-desktop", // NON-NLS
    help = i18n("main.help.title")
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val debug by option(
        "--debug", "-d", // NON-NLS
        help = i18n("main.help.debug")
    ).flag(default = false)
    private val verbosity by option(
        "--verbose", // NON-NLS
        "-v", // NON-NLS
        help = i18n("main.help.verbose")
    ).counted()
    private val dataDir by option(
        "--data-dir", // NON-NLS
        help = i18nF("main.help.data", DEFAULT_DATA_DIR),
        metavar = "PATH",
        envvar = "BRIAR_DATA_DIR" // NON-NLS
    ).default(DEFAULT_DATA_DIR)
    private val socksPort by option(
        "--socks-port", // NON-NLS
        help = i18n("main.help.tor.port.socks")
    ).int().default(DEFAULT_SOCKS_PORT)
    private val controlPort by option(
        "--control-port", // NON-NLS
        help = i18n("main.help.tor.port.control")
    ).int().default(DEFAULT_CONTROL_PORT)

    @OptIn(ExperimentalComposeUiApi::class)
    override fun run() {
        val level = if (debug) ALL else when (verbosity) {
            0 -> WARNING
            1 -> INFO
            else -> ALL
        }

        LogUtils.setupLogging(level)

        val buildTime = Instant.ofEpochMilli(BuildData.GIT_TIME).atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // NON-NLS
        LOG.i { "This is briar-desktop version ${BuildData.VERSION}" }
        LOG.i { "Build info:" }
        LOG.i { "  Git hash ${BuildData.GIT_HASH}" }
        LOG.i { "  Commit time ${formatter.format(buildTime)}" }
        LOG.i { "  Branch ${BuildData.GIT_BRANCH}" }

        val dataDir = getDataDir()
        val app =
            DaggerBriarDesktopApp.builder().desktopModule(
                DesktopModule(dataDir, socksPort, controlPort)
            ).build()
        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        val eventExecutor = app.getEventExecutor()
        Thread {
            while (true) {
                LOG.i { "Background tick" }
                eventExecutor.execute { LOG.i { "Foreground tick " } }
                try {
                    Thread.sleep(1000)
                } catch (ignored: InterruptedException) {
                    break
                }
            }
        }.start()

        application {
            app.getBriarUi().start {
                app.getBriarUi().stop()
                exitApplication()
            }
        }
    }

    private fun getDataDir(): Path {
        val file = Paths.get(dataDir)
        if (!Files.exists(file)) {
            Files.createDirectories(file)
            if (!Files.exists(file)) {
                throw IOException("Could not create directory: ${file.toAbsolutePath()}")
            }
        }
        if (!Files.isDirectory(file)) {
            throw IOException("Data dir is not a directory: ${file.toAbsolutePath()}")
        }
        FileUtils.setRWX(file)
        return file
    }
}

fun main(args: Array<String>) = Main().main(args)
