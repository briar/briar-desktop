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

package org.briarproject.briar.desktop.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * An event to be emitted from anywhere and reacted to in a Composable.
 * The class is backed by a [MutableState] and thus thread-safe.
 * <p>
 * Note that only one Composable will be able to react to the event,
 * trying to react to it from multiple places is considered a bug.
 * <p>
 * As emitting one-time events instead of updating state goes against
 * the declarative programming paradigm of Compose,
 * only use this class if you are sure that you actually need it.
 */
class SingleStateEvent<T : Any> {
    /**
     * Internal representation of state. Please don't use this directly!
     */
    var state = mutableStateOf<T?>(null)

    /**
     * Emit a new value of type [T] for this event.
     */
    fun emit(value: T) {
        state.value = value
    }

    /**
     * React to every new value of type [T] emitted through this event,
     * by directly reading the state in the calling function.
     * This can be used to invalidate a composable function.
     * Make sure to not react to the same event on multiple places.
     */
    inline fun react(block: (T) -> Unit) {
        val value = state.value
        if (value != null) {
            state.value = null
            block.invoke(value)
        }
    }

    /**
     * React to every new value of type [T] emitted through this event
     * inside a LaunchedEffect.
     * Make sure to not react to the same event on multiple places.
     */
    @Composable
    fun reactInCoroutine(block: (T) -> Unit) {
        LaunchedEffect(state.value) {
            react(block)
        }
    }
}
