package org.briarproject.briar.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import java.io.File
import java.io.File.separator
import java.io.IOException
import java.lang.System.getProperty
import java.nio.file.Files.setPosixFilePermissions
import java.nio.file.attribute.PosixFilePermission
import java.util.logging.Level
import java.util.logging.LogManager

fun main() = Window {
    LogManager.getLogManager().getLogger("").level = Level.INFO

    val dataDir = getDataDir()
    val app =
        DaggerBriarSwingApp.builder().swingModule(
            SwingModule(
                dataDir
            )
        ).build()
    // We need to load the eager singletons directly after making the
    // dependency graphs
    BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
    BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

    app.getUI().startBriar()
    app.getUI().startUI()

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TheImage()
        Spacer(Modifier.height(32.dp))
        TheText()
        TheButton()
    }
}

private fun getDataDir(): File {
    val file = File(getProperty("user.home") + separator + ".briar")
    if (!file.exists() && !file.mkdirs()) {
        throw IOException("Could not create directory: ${file.absolutePath}")
    } else if (!file.isDirectory) {
        throw IOException("Data dir is not a directory: ${file.absolutePath}")
    }
    val perms = HashSet<PosixFilePermission>()
    perms.add(PosixFilePermission.OWNER_READ)
    perms.add(PosixFilePermission.OWNER_WRITE)
    perms.add(PosixFilePermission.OWNER_EXECUTE)
    setPosixFilePermissions(file.toPath(), perms)
    return file
}

@Composable
private fun TheButton() {
    var text by remember { mutableStateOf("Start chatting") }
    Button(onClick = {
        text = "Sorry, not yet available"
    }) {
        Text(text)
    }
}

@Composable
private fun TheImage() {
    Image(
        painter = svgResource("images/logo_circle.svg"),
        contentDescription = "Briar logo",
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(400.dp))
    )
}

@Composable
private fun TheText() {
    Text("Welcome to Briar")
}