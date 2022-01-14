package org.briarproject.briar.desktop.login

class ErrorSubViewModel(
    private val viewModel: StartupViewModel,
    val error: Error,
    val onBackButton: () -> Unit,
) : StartupViewModel.SubViewModel {
    sealed interface Error
}
