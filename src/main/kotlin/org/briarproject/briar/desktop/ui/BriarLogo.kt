package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun BriarLogo(modifier: Modifier = Modifier.fillMaxWidth()) =
    Image(painterResource("images/logo_circle.svg"), i18n("access.logo"), modifier)
