package org.briarproject.briar.desktop.viewmodel

import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager

abstract class EventListenerDbViewModel(
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val eventBus: EventBus
) : EventListener, DbViewModel(briarExecutors, lifecycleManager, db) {

    override fun onInit() {
        eventBus.addListener(this)
    }

    override fun onCleared() {
        eventBus.removeListener(this)
    }
}
