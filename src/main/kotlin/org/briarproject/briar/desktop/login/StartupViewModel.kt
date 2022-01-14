package org.briarproject.briar.desktop.login

import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.ALREADY_RUNNING
import org.briarproject.bramble.api.lifecycle.LifecycleManager.StartResult.SUCCESS
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class StartupViewModel
@Inject
constructor(
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    private val lifecycleManager: LifecycleManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    sealed interface ViewHolder {
        fun lifecycleStateChanged(s: LifecycleManager.LifecycleState) {}
    }

    class StartingError(val error: LifecycleManager.StartResult):
        ErrorViewHolder.Error

    private val _mode = mutableStateOf(decideMode())
    val mode = _mode.asState()

    private fun decideMode() =
        if (accountManager.accountExists()) makeLogin()
        else makeRegistration()

    private fun makeLogin() = LoginViewHolder(
        this, accountManager, briarExecutors, lifecycleManager.lifecycleState
    )

    fun showLogin() {
        _mode.value = makeLogin()
    }

    private fun makeRegistration() = RegistrationViewHolder(
        this, accountManager, briarExecutors, passwordStrengthEstimator
    )

    fun showRegistration() {
        _mode.value = makeRegistration()
    }

    private fun makeError(error: ErrorViewHolder.Error) = ErrorViewHolder(
        this, error, onBackButton = { _mode.value = decideMode() }
    )
    fun showError(error: ErrorViewHolder.Error) {
        _mode.value = makeError(error)
    }

    override fun eventOccurred(e: Event) {
        if (e is LifecycleEvent) _mode.value.lifecycleStateChanged(e.lifecycleState)
    }

    @IoExecutor
    fun startBriarCore() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        val result = lifecycleManager.startServices(dbKey)
        when (result) {
            SUCCESS -> lifecycleManager.waitForStartup()
            ALREADY_RUNNING -> LOG.info { "Already running" }
            else -> {
                LOG.warn { "Startup failed: $result" }
                showError(StartingError(result))
            }
        }
    }
}
