package org.briarproject.briar.desktop.viewmodel

interface ViewModel {

    /**
     * Called to initialize the [ViewModel] as soon as it is first used
     * inside a Composable function.
     */
    fun onInit() {}

    /**
     * Called to clear the [ViewModel] as soon as the calling
     * Composable function goes out of scope.
     */
    fun onCleared() {}
}
