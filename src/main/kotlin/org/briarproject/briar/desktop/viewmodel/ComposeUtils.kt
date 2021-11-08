package org.briarproject.briar.desktop.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.briarproject.briar.desktop.ui.LocalViewModelProvider
import kotlin.reflect.KClass

/**
 * Returns an existing [ViewModel] or creates a new one
 *
 * The [ViewModel] is created and retained in the given [viewModelProvider],
 * that defaults to [LocalViewModelProvider].
 * It will be automatically initialized as soon as the calling screen is composed
 * for the first time, and cleared when it goes out of scope.
 *
 * @param viewModelProvider The scope that the created [ViewModel] should be associated with.
 * @param key The key to use to identify the [ViewModel].
 * @return A [ViewModel] that is an instance of the given [VM] type.
 */
@Composable
inline fun <reified VM : ViewModel> viewModel(
    key: String? = null,
    viewModelProvider: ViewModelProvider = checkNotNull(LocalViewModelProvider.current) {
        "No ViewModelProvider was provided via LocalViewModelProvider"
    }
): VM = viewModel(VM::class, key, viewModelProvider)

/**
 * Returns an existing [ViewModel] or creates a new one
 *
 * The [ViewModel] is created and retained in the given [viewModelProvider],
 * that defaults to [LocalViewModelProvider].
 * It will be automatically initialized as soon as the calling screen is composed
 * for the first time, and cleared when it goes out of scope.
 *
 * @param modelClass The class of the [ViewModel] to create an instance of it if it is not
 * present.
 * @param viewModelProvider The scope that the created [ViewModel] should be associated with.
 * @param key The key to use to identify the [ViewModel].
 * @return A [ViewModel] that is an instance of the given [VM] type.
 */
@Composable
fun <VM : ViewModel> viewModel(
    modelClass: KClass<VM>,
    key: String? = null,
    viewModelProvider: ViewModelProvider = checkNotNull(LocalViewModelProvider.current) {
        "No ViewModelProvider was provided via LocalViewModelProvider"
    }
): VM {
    val viewModel = if (key == null) {
        viewModelProvider.get(modelClass)
    } else {
        viewModelProvider.get(key, modelClass)
    }

    DisposableEffect(key) {
        viewModel.onInit()

        onDispose {
            viewModel.onCleared()
        }
    }

    return viewModel
}
