package org.briarproject.briar.desktop.login

import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.lifecycle.LifecycleManager

class StartupFailedEvent(val result: LifecycleManager.StartResult) : Event()
