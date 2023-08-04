/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.viewmodel

abstract class ViewModel {

    private var inComposition = false

    /**
     * Called to initialize the [ViewModel] as soon as it is first used
     * inside a Composable function.
     *
     * If you want to specify initialization code, overwrite [onInit] instead.
     */
    fun onEnterComposition() {
        if (inComposition)
            throw RuntimeException("Injecting the same instance of ${this::class.simpleName} in different Composables is not permitted.")
        inComposition = true
        onInit()
    }

    /**
     * Called to clear the [ViewModel] as soon as the calling
     * Composable function goes out of scope.
     *
     * If you want to specify de-initialization code, overwrite [onCleared] instead.
     */
    fun onExitComposition() {
        if (!inComposition)
            throw RuntimeException("Wrong use of ViewModel.")
        inComposition = false
        onCleared()
    }

    /**
     * Called to initialize the [ViewModel] as soon as it is first used
     * inside a Composable function.
     *
     * This function can be overridden in child classes,
     * but implementations should always call `super.onInit()` first.
     *
     * Apart from that, **do not call this function manually anywhere.**
     */
    open fun onInit() {}

    /**
     * Called to clear the [ViewModel] as soon as the calling
     * Composable function goes out of scope.
     *
     * This function can be overridden in child classes,
     * but implementations should always call `super.onCleared()` first.
     *
     * Apart from that, **do not call this function manually anywhere.**
     */
    open fun onCleared() {}
}
