package org.briarproject.briar.desktop.login

import org.briarproject.bramble.api.lifecycle.LifecycleManager

class ErrorViewHolder(
    private val viewModel: StartupViewModel,
    val error: LifecycleManager.StartResult,
    val onBackButton: () -> Unit,
) : StartupViewModel.ViewHolder
