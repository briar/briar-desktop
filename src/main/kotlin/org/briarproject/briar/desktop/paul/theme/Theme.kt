package org.briarproject.briar.desktop.paul.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val graySurface = Color(0xFF2A2A2A)
val lightGray = Color(0xFFD3D3D3)
val briarGray = Color(0xff222222)
val briarBlack = Color(0xff1E2228)
val briarSelBlack = Color(0xff495261)
val briarDarkGray = Color(0xFF3D4552)
val darkGray = Color(0xFF151515)

// val divider = Color(0xff35383D)
val briarBlue = Color(0xFF2D3E50)
val briarLightBlue = Color(0xFFEBEFF2)
val briarGreen = Color(0xff61d800)
val briarBlueMsg = Color(0xFF1b69b6)
val briarBlueSpecialMsg = Color(0xFF134a80)
val briarGrayMsg = Color(0xff3b4047)
val briarGraySpecialMsg = Color(0xFF212d3b)
val briarPrimary = Color(0xff1f78d1)

val Colors.placeholder: Color
    @Composable get() = if (isLight) Gray700 else Gray300

val Colors.divider: Color
    @Composable get() = if (isLight) Gray300 else Gray900
val Colors.outline: Color
    @Composable get() = Color.White

val Colors.surfaceVariant: Color
    @Composable get() = if (isLight) Gray100 else Gray950
val Colors.sidebarSurface: Color
    @Composable get() = if (isLight) Gray300 else Gray900
val Colors.topBarSurface: Color
    @Composable get() = if (isLight) Color.Red else Color.Blue
val Colors.selectedCard: Color
    @Composable get() = if (isLight) Gray400 else Gray700

val Colors.localMsgBubble: Color
    @Composable get() = if (isLight) briarPrimary else briarPrimary
val Colors.awayMsgBubble: Color
    @Composable get() = if (isLight) Gray300 else Gray800

val DarkColors = darkColors(
    primary = Color(0xff1f78d1),
    primaryVariant = Color(0xff435b77),
    secondary = Lime500,
    background = Color(0xff121212),
    surface = Color(0xff121212),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xffb00020),
)

val LightColors = lightColors(
    primary = Color(0xff1f78d1),
    primaryVariant = Color(0xff435b77),
    secondary = Lime300,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color(0xffb00020),
)
