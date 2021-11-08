package org.briarproject.briar.desktop.viewmodel

import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener

abstract class BriarEventListenerViewModel(
    private val eventBus: EventBus
) : ViewModel, EventListener {

    override fun onInit() {
        eventBus.addListener(this)
    }

    override fun onCleared() {
        eventBus.removeListener(this)
    }
}
