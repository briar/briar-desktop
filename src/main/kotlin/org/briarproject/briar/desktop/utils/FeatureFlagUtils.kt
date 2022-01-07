package org.briarproject.briar.desktop.utils

import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.ui.LocalDesktopFeatureFlags

@Composable
fun getDesktopFeatureFlags() = checkNotNull(LocalDesktopFeatureFlags.current) {
    "No DesktopFeatureFlags was provided via LocalDesktopFeatureFlags"
}
