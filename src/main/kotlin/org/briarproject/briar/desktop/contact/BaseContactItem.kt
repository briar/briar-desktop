package org.briarproject.briar.desktop.contact

sealed interface BaseContactItem {

    val idWrapper: ContactIdWrapper
    val displayName: String
    val timestamp: Long
}
