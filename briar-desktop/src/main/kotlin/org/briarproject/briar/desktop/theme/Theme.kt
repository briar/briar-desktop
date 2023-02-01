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

package org.briarproject.briar.desktop.theme

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
val Colors.warningBackground get() = Red500
val Colors.warningForeground get() = Color.White
val Colors.sendButton get() = if (isLight) Lime700 else Lime500
val Colors.passwordStrengthWeak get() = Red500
val Colors.passwordStrengthMiddle get() = if (isLight) Orange700 else Orange500
val Colors.passwordStrengthStrong get() = if (isLight) Lime700 else Lime500

val DarkColors = darkColors(
    primary = Blue500,
    primaryVariant = Night500,
    secondary = Lime500,
    secondaryVariant = Lime500,
    background = materialDarkBg,
    surface = materialDarkBg,
    error = DeepOrange400,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White,
)
val LightColors = lightColors(
    primary = Blue700,
    primaryVariant = Night500,
    secondary = Lime300,
    secondaryVariant = Lime500,
    background = Color.White,
    surface = Color.White,
    error = DeepOrange500,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
)

val robotoRegular = FontFamily(
    Font(resource = "fonts/Roboto-Regular.ttf"), // NON-NLS
)

val robotoMedium = FontFamily(
    Font(resource = "fonts/Roboto-Medium.ttf"), // NON-NLS
)

val spacing = 0.3.sp

val briarTypography = Typography(
    defaultFontFamily = robotoRegular,
    h1 = TextStyle(letterSpacing = spacing, fontFamily = robotoMedium, fontSize = 36.sp),
    h2 = TextStyle(letterSpacing = spacing, fontFamily = robotoMedium, fontSize = 24.sp),
    h3 = TextStyle(letterSpacing = spacing, fontFamily = robotoMedium, fontSize = 20.sp),
    h4 = TextStyle(letterSpacing = spacing, fontFamily = robotoMedium, fontSize = 18.sp),
    h5 = TextStyle(letterSpacing = spacing, fontFamily = robotoMedium, fontSize = 16.sp),
    h6 = TextStyle(letterSpacing = spacing, fontFamily = robotoMedium, fontSize = 14.sp),
    subtitle1 = TextStyle(letterSpacing = spacing, fontSize = 12.sp),
    subtitle2 = TextStyle(letterSpacing = spacing, fontSize = 10.sp),
    body1 = TextStyle(letterSpacing = spacing, fontSize = 14.sp),
    body2 = TextStyle(letterSpacing = spacing, fontSize = 14.sp),
    button = TextStyle(letterSpacing = spacing, fontSize = 14.sp),
    caption = TextStyle(letterSpacing = spacing, fontSize = 10.sp),
    overline = TextStyle(letterSpacing = spacing, fontSize = 10.sp),
)

@Composable
fun BriarTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: Colors? = null,
    content: @Composable () -> Unit,
) = MaterialTheme(
    colors = colors ?: if (isDarkTheme) DarkColors else LightColors,
    typography = briarTypography,
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colors.secondary,
        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.4f)
    )
    val contextMenuRepresentation = if (isDarkTheme) {
        DarkDefaultContextMenuRepresentation
    } else {
        LightDefaultContextMenuRepresentation
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides customTextSelectionColors,
        LocalContextMenuRepresentation provides contextMenuRepresentation,
    ) {
        Surface {
            content()
        }
    }
}
