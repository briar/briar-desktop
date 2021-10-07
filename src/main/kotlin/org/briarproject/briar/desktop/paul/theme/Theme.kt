package org.briarproject.briar.desktop.paul.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Colors.divider: Color get() = if (isLight) Gray300 else Gray800
val Colors.outline: Color get() = if (isLight) Gray900 else Gray200
val Colors.surfaceVariant: Color get() = if (isLight) Gray100 else Gray950
val Colors.sidebarSurface: Color get() = if (isLight) Gray200 else Gray900
val Colors.selectedCard: Color get() = if (isLight) Gray400 else Gray700
val Colors.localMsgBubble: Color get() = Blue500
val Colors.awayMsgBubble: Color get() = if (isLight) Gray300 else Gray800

val DarkColors = darkColors(
    primary = Blue500,
    primaryVariant = Night500,
    secondary = Lime500,
    background = materialDarkBg,
    surface = materialDarkBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = briarError
)
val LightColors = lightColors(
    primary = Blue500,
    primaryVariant = Night500,
    secondary = Lime300,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = briarError
)

@Composable
fun BriarTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: Colors? = null,
    content: @Composable () -> Unit
) {
    val myColors = colors ?: if (isDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colors = myColors,
        content = content,
    )
}
