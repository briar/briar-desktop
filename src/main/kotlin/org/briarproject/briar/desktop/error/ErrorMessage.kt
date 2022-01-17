package org.briarproject.briar.desktop.error

class ErrorMessage(val type: Type, val message: String) {

    enum class Type {
        ERROR,
        WARNING
    }
}
