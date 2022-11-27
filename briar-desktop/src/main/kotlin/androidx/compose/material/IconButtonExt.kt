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

package androidx.compose.material

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.Tooltip

/**
 * IconButton is a clickable icon, used to represent actions. An IconButton has an overall minimum
 * touch target size of 48 x 48dp, to meet accessibility guidelines.
 * This version of [IconButton] enables tooltips on Desktop Devices and internally uses an [Icon] to show [icon].
 *
 * @param icon [ImageVector] to draw as icon inside this IconButton
 * @param contentDescription text used by accessibility services and the tooltip
 * to describe the action invoked by this IconButton.
 * @param onClick the lambda to be invoked when this icon is pressed
 * @param modifier optional [Modifier] for this IconButton
 * @param iconTint tint to be applied to [icon]. See [Icon] for more information.
 * @param enabled whether or not this IconButton will handle input events and appear enabled for
 * semantics purposes
 * @param interactionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for this IconButton. You can create and pass in your own remembered
 * [MutableInteractionSource] if you want to observe [Interaction]s and customize the
 * appearance / behavior of this IconButton in different [Interaction]s.
 * @param extraContent content that is added after the [icon]. This is mainly used for showing [DropdownMenu]s.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    iconTint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    extraContent: (@Composable () -> Unit)? = null,
) = Tooltip(
    text = contentDescription,
    modifier = modifier,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Icon(
            icon,
            contentDescription,
            Modifier.requiredSize(iconSize),
            iconTint
        )
        extraContent?.invoke()
    }
}
