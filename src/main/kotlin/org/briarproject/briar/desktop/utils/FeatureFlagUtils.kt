package org.briarproject.briar.desktop.utils

import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.DesktopFeatureFlags
import org.briarproject.briar.desktop.ui.LocalDesktopFeatureFlags

@Composable
fun getDesktopFeatureFlags(): DesktopFeatureFlags {
    val flags = LocalDesktopFeatureFlags.current
    checkNotNull(flags) {
        "No DesktopFeatureFlags was provided via LocalDesktopFeatureFlags"
    }
    return flags
}
