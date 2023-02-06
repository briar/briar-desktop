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

package org.briarproject.briar.desktop.mailbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var cache: ImageVector? = null
val MailboxIcon: ImageVector
    get() = cache ?: ImageVector.Builder(
        name = "ic_mailbox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).path(
        fill = SolidColor(Color.Black),
        fillAlpha = 1f,
        stroke = null,
        strokeAlpha = 1f,
        strokeLineWidth = 1f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Bevel,
        strokeLineMiter = 1f,
        pathFillType = PathFillType.NonZero
    ) {
        moveToRelative(5.0782f, 2.0f)
        curveToRelative(-0.5245f, 0.0f, -1.0278f, 0.2105f, -1.3987f, 0.5848f)
        curveTo(3.3085f, 2.959f, 3.1f, 3.4663f, 3.1f, 3.9956f)
        verticalLineTo(16.7677f)
        curveToRelative(0.0f, 0.5293f, 0.2085f, 1.0377f, 0.5794f, 1.412f)
        curveToRelative(0.3709f, 0.3742f, 0.8742f, 0.5836f, 1.3987f, 0.5836f)
        horizontalLineTo(15.8509f)
        lineToRelative(3.589f, 3.0261f)
        curveTo(20.0193f, 22.278f, 20.9f, 21.862f, 20.9f, 21.1001f)
        verticalLineTo(3.9956f)
        curveTo(20.9f, 3.4663f, 20.6915f, 2.959f, 20.3206f, 2.5848f)
        curveTo(19.9497f, 2.2105f, 19.4464f, 2.0f, 18.9218f, 2.0f)
        close()
        moveTo(6.2648f, 5.1927f)
        horizontalLineTo(17.7352f)
        verticalLineToRelative(5.6502f)
        horizontalLineToRelative(-4.0073f)
        verticalLineToRelative(1.9542f)
        horizontalLineToRelative(1.4659f)
        curveToRelative(0.2643f, 0.0f, 0.3966f, 0.3227f, 0.2098f, 0.5113f)
        lineToRelative(-3.1602f, 3.1881f)
        curveToRelative(-0.1159f, 0.1169f, -0.3036f, 0.1169f, -0.4195f, 0.0f)
        lineTo(8.6637f, 13.3084f)
        curveTo(8.4768f, 13.1198f, 8.6091f, 12.7971f, 8.8734f, 12.7971f)
        horizontalLineTo(10.3394f)
        verticalLineTo(10.8429f)
        horizontalLineTo(6.2648f)
        close()
    }.build().also { cache = it }
