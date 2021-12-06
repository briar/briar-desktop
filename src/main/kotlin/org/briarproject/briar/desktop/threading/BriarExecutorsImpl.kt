package org.briarproject.briar.desktop.threading

import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.lifecycle.IoExecutor
import java.util.concurrent.Executor
import javax.inject.Inject

class BriarExecutorsImpl
@Inject
constructor(
    @UiExecutor private val uiExecutor: Executor,
    @DatabaseExecutor private val dbExecutor: Executor,
    @IoExecutor private val ioExecutor: Executor,
) : BriarExecutors {
    override fun onDbThread(@DatabaseExecutor task: () -> Unit) = dbExecutor.execute(task)

    override fun onUiThread(@UiExecutor task: () -> Unit) = uiExecutor.execute(task)

    override fun onIoThread(@IoExecutor task: () -> Unit) = ioExecutor.execute(task)
}
