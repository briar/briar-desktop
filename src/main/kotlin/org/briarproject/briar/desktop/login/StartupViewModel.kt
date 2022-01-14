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

    sealed interface SubViewModel {
        fun lifecycleStateChanged(s: LifecycleManager.LifecycleState) {}
    }

    class StartingError(val error: LifecycleManager.StartResult) :
        ErrorSubViewModel.Error

    private val _currentSubViewModel = mutableStateOf(decideSubViewModel())
    val currentSubViewModel = _currentSubViewModel.asState()

    private fun decideSubViewModel() =
        if (accountManager.accountExists()) makeLogin()
        else makeRegistration()

    private fun makeLogin() = LoginSubViewModel(
        this, accountManager, briarExecutors, lifecycleManager.lifecycleState
    )

    fun showLogin() {
        _currentSubViewModel.value = makeLogin()
    }

    private fun makeRegistration() = RegistrationSubViewModel(
        this, accountManager, briarExecutors, passwordStrengthEstimator
    )

    fun showRegistration() {
        _currentSubViewModel.value = makeRegistration()
    }

    private fun makeError(error: ErrorSubViewModel.Error) = ErrorSubViewModel(
        this, error, onBackButton = { _currentSubViewModel.value = decideSubViewModel() }
    )

    fun showError(error: ErrorSubViewModel.Error) {
        _currentSubViewModel.value = makeError(error)
    }

    override fun eventOccurred(e: Event) {
        if (e is LifecycleEvent) _currentSubViewModel.value.lifecycleStateChanged(e.lifecycleState)
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
