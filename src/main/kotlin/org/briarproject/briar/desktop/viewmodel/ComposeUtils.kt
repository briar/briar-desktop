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

/*
Code inspired by and adapted to our needs from
https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:lifecycle/lifecycle-viewmodel-compose/src/main/java/androidx/lifecycle/viewmodel/compose/ViewModel.kt;drc=0c44ec9ae8a43abafd966cd130196e9334fad359
licensed under the Apache License, Version 2.0
 */
package org.briarproject.briar.desktop.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    val viewModel = viewModelProvider.get(modelClass, key)

    DisposableEffect(key) {
        viewModel.onInit()

        onDispose {
            viewModel.onCleared()
        }
    }

    return viewModel
}

/**
 * Returns this [MutableState] as an immutable [State].
 */
fun <T> MutableState<T>.asState(): State<T> = this

/**
 * Returns this [SnapshotStateList] as an immutable [List].
 */
fun <T> SnapshotStateList<T>.asList(): List<T> = this
