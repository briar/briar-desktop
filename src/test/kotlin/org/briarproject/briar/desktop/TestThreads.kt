package org.briarproject.briar.desktop

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension
import javax.swing.SwingUtilities

fun main() {

    application {
        SwingUtilities.invokeLater {
            println("invokeLater: ${Thread.currentThread().name}")
        }
        Window(
            title = "Testing Threads",
            onCloseRequest = { exitApplication() },
            icon = painterResource("images/logo_circle.svg")
        ) {
            window.minimumSize = Dimension(800, 600)
            Button(
                onClick = {
                    println("Click: ${Thread.currentThread().name}")
                },
            ) {
                Text("Button")
            }
        }
    }
}
