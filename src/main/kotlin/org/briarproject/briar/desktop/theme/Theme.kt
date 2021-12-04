package org.briarproject.briar.desktop.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

val Colors.divider: Color get() = if (isLight) Gray300 else Gray800
val Colors.outline: Color get() = if (isLight) Gray900 else Gray200
val Colors.surfaceVariant: Color get() = if (isLight) Gray100 else Gray950
val Colors.sidebarSurface: Color get() = if (isLight) Gray200 else Gray900
val Colors.selectedCard: Color get() = if (isLight) Gray400 else Gray700
val Colors.msgStroke: Color get() = if (isLight) Gray300 else Gray900
val Colors.msgIn: Color get() = if (isLight) Color.White else Night700
val Colors.msgOut: Color get() = if (isLight) Blue400 else Blue600
val Colors.noticeIn: Color get() = if (isLight) Night50 else Night800
val Colors.noticeOut: Color get() = if (isLight) Blue600 else Blue800
val Colors.textPrimary: Color get() = if (isLight) TextPrimaryMaterialLight else TextPrimaryMaterialDark
val Colors.textSecondary: Color get() = if (isLight) TextSecondaryMaterialLight else TextSecondaryMaterialDark
val Colors.privateMessageDate: Color get() = Gray200
val Colors.buttonTextNegative: Color get() = Red500
val Colors.buttonTextPositive: Color get() = Blue400

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
val interRegular = FontFamily(
    Font(resource = "fonts/Inter-Regular.ttf")
)
val interLight = FontFamily(
    Font(resource = "fonts/Inter-Light.ttf")
)
val interMedium = FontFamily(
    Font(resource = "fonts/Inter-Medium.ttf")
)
// Reference: https://briar-styleguide.netlify.app/design/#typography
val briarTypography = Typography(
    defaultFontFamily = interRegular,
    h1 = TextStyle(fontFamily = interLight, fontSize = 96.sp, letterSpacing = (-1.5).sp),
    h2 = TextStyle(fontFamily = interLight, fontSize = 60.sp, letterSpacing = (-0.5).sp),
    h3 = TextStyle(fontFamily = interRegular, fontSize = 48.sp, letterSpacing = 0.sp),
    h4 = TextStyle(fontFamily = interRegular, fontSize = 34.sp, letterSpacing = 0.25.sp),
    h5 = TextStyle(fontFamily = interRegular, fontSize = 24.sp, letterSpacing = 0.sp),
    h6 = TextStyle(fontFamily = interMedium, fontSize = 20.sp, letterSpacing = 0.15.sp),
    subtitle1 = TextStyle(fontFamily = interRegular, fontSize = 16.sp, letterSpacing = 0.15.sp),
    subtitle2 = TextStyle(fontFamily = interMedium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    body1 = TextStyle(fontFamily = interRegular, fontSize = 16.sp, letterSpacing = 0.sp),
    body2 = TextStyle(fontFamily = interRegular, fontSize = 14.sp, letterSpacing = 0.sp),
    button = TextStyle(fontFamily = interMedium, fontSize = 14.sp, letterSpacing = 0.5.sp),
    caption = TextStyle(fontFamily = interRegular, fontSize = 12.sp, letterSpacing = 0.4.sp),
    overline = TextStyle(fontFamily = interRegular, fontSize = 10.sp, letterSpacing = 1.5.sp),
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
        typography = briarTypography,
    )
}
