package org.briarproject.briar.desktop.threading

import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.lifecycle.IoExecutor

interface BriarExecutors {
    fun onDbThread(@DatabaseExecutor task: () -> Unit)

    fun onUiThread(@UiExecutor task: () -> Unit)

    fun onIoThread(@IoExecutor task: () -> Unit)
}
