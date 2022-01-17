package org.briarproject.briar.desktop.error

import androidx.compose.runtime.mutableStateListOf
import mu.KotlinLogging
import org.briarproject.briar.desktop.viewmodel.asList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorManager @Inject internal constructor() {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _errors = mutableStateListOf<ErrorMessage>()
    val errors = _errors.asList()

    fun addError(message: ErrorMessage) {
        LOG.warn { message }
        _errors.add(message)
    }

    fun clearError(message: ErrorMessage) {
        _errors.remove(message)
    }
}
