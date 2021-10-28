package org.briarproject.briar.desktop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.utils.FileUtils
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import java.io.File.separator
import java.io.IOException
import java.lang.System.getProperty
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Level.ALL
import java.util.logging.Level.INFO
import java.util.logging.Level.WARNING
import java.util.logging.LogManager

private val DEFAULT_DATA_DIR = getProperty("user.home") + separator + ".briar" + separator + "desktop"

private class Main : CliktCommand(
    name = "briar-desktop",
    help = i18n("main.help.title")
) {
    private val debug by option("--debug", "-d", help = i18n("main.help.debug")).flag(
        default = false
    )
    private val verbosity by option(
        "--verbose",
        "-v",
        help = i18n("main.help.verbose")
    ).counted()
    private val dataDir by option(
        "--data-dir",
        help = i18nF("main.help.data", DEFAULT_DATA_DIR),
        metavar = "PATH",
        envvar = "BRIAR_DATA_DIR"
    ).default(DEFAULT_DATA_DIR)

    @OptIn(ExperimentalComposeUiApi::class)
    override fun run() {
        val level = if (debug) ALL else when (verbosity) {
            0 -> WARNING
            1 -> INFO
            else -> ALL
        }

        LogManager.getLogManager().getLogger("").level = level

        val dataDir = getDataDir()
        val app =
            DaggerBriarDesktopApp.builder().desktopModule(
                DesktopModule(dataDir)
            ).build()
        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        application {
            app.getBriarUi().start(this)
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
