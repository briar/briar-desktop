package org.briarproject.briar.desktop.login

import mu.KotlinLogging
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.ALREADY_RUNNING
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.SUCCESS

object StartupUtils {

    private val LOG = KotlinLogging.logger {}

    @IoExecutor
    fun startBriarCore(accountManager: AccountManager, lifecycleManager: LifecycleManager, eventBus: EventBus) {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        val result = lifecycleManager.startServices(dbKey)
        when (result) {
            SUCCESS -> lifecycleManager.waitForStartup()
            ALREADY_RUNNING -> LOG.info { "Already running" }
            else -> {
                LOG.warn { "Startup failed: $result" }
                eventBus.broadcast(StartupFailedEvent(result))
            }
        }
    }
}
