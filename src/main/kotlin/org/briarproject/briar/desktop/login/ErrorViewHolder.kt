package org.briarproject.briar.desktop.login

import org.briarproject.bramble.api.lifecycle.LifecycleManager

class ErrorViewHolder(
    private val viewModel: StartupViewModel,
    val error: Error,
    val onBackButton: () -> Unit,
) : StartupViewModel.ViewHolder {
    sealed interface Error
}
