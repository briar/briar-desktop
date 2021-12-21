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
    private var state = mutableStateOf<T?>(null)

    /**
     * Emit a new value of type [T] for this event.
     */
    fun emit(value: T) {
        state.value = value
    }

    /**
     * React to every new value of type [T] emitted through this event.
     * Make sure to not react to the same event on multiple places.
     */
    @Composable
    fun react(block: (T) -> Unit) {
        LaunchedEffect(state.value) {
            val value = state.value
            if (value != null) {
                block(value)
                state.value = null
            }
        }
    }
}
