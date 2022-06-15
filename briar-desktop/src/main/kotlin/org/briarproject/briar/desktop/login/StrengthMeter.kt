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

package org.briarproject.briar.desktop.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_STRONG
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK
import org.briarproject.briar.desktop.theme.passwordStrengthMiddle
import org.briarproject.briar.desktop.theme.passwordStrengthStrong
import org.briarproject.briar.desktop.theme.passwordStrengthWeak
import org.briarproject.briar.desktop.utils.PreviewUtils
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "strength" to PreviewUtils.FloatSlider(0f, 0f, 1f)
) {
    StrengthMeter(getFloatParameter("strength"))
    Spacer(Modifier.height(8.dp))
    OutlinedTextField("test", {})
}

@Composable
fun StrengthMeter(
    strength: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        strength < QUITE_WEAK -> MaterialTheme.colors.passwordStrengthWeak
        strength < QUITE_STRONG -> MaterialTheme.colors.passwordStrengthMiddle
        else -> MaterialTheme.colors.passwordStrengthStrong
    }
    val animatedProgress by animateFloatAsState(
        targetValue = strength,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    val animatedColor by animateColorAsState(color)
    LinearProgressIndicator(
        progress = animatedProgress,
        color = animatedColor,
        modifier = modifier
            .heightIn(min = 12.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity),
                shape = MaterialTheme.shapes.small
            )
    )
}
