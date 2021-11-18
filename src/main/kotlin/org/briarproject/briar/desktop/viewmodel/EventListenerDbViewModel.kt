package org.briarproject.briar.desktop.viewmodel

import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import java.util.concurrent.Executor

abstract class EventListenerDbViewModel(
    @UiExecutor uiExecutor: Executor,
    @DatabaseExecutor dbExecutor: Executor,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val eventBus: EventBus
) : EventListener, DbViewModel(uiExecutor, dbExecutor, lifecycleManager, db) {

    override fun onInit() {
        eventBus.addListener(this)
    }

    override fun onCleared() {
        eventBus.removeListener(this)
    }
}
