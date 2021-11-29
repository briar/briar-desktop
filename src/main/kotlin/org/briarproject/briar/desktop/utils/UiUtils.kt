package org.briarproject.briar.desktop.utils

object UiUtils {
    fun getContactDisplayName(name: String, alias: String?) =
        if (alias == null) name else "$alias ($name)"
}
