package org.briarproject.briar.desktop.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_STRONG
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.STRONG
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.WEAK
import org.briarproject.briar.desktop.utils.PreviewUtils
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

val RED = Color(255, 0, 0)
val ORANGE = Color(255, 160, 0)
val YELLOW = Color(255, 255, 0)
val LIME = Color(180, 255, 0)
val GREEN = Color(0, 255, 0)

fun main() = preview(
    "strength" to PreviewUtils.FloatSlider(0f, 0f, 1f)
) {
    StrengthMeter(getFloatParameter("strength"))
}

@Composable
fun StrengthMeter(
    strength: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        strength < WEAK -> RED
        strength < QUITE_WEAK -> ORANGE
        strength < QUITE_STRONG -> YELLOW
        strength < STRONG -> GREEN
        else -> LIME
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
    )
}
