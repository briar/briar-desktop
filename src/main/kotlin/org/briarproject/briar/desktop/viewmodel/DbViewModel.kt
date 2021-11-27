package org.briarproject.briar.desktop.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.DbCallable
import org.briarproject.bramble.api.db.DbRunnable
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager

abstract class DbViewModel(
    private val briarExecutors: BriarExecutors,
    private val lifecycleManager: LifecycleManager,
    private val db: TransactionManager
) : ViewModel {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    /**
     * Waits for the DB to open and runs the given [task] on the [DatabaseExecutor].
     *
     * For thread-safety, do not access composable [State] inside [task],
     * but use local variables and [MutableState.postValue] instead.
     */
    protected fun runOnDbThread(task: () -> Unit) = briarExecutors.onDbThread {
        try {
            lifecycleManager.waitForDatabase()
            task()
        } catch (e: InterruptedException) {
            LOG.warn("Interrupted while waiting for database")
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            LOG.warn(e) { "Unhandled exception in database executor" }
        }
    }

    /**
     * Waits for the DB to open and runs the given [task] on the [DatabaseExecutor],
     * providing a [Transaction], that may be [readOnly] or not, to the task.
     *
     * For thread-safety, do not access composable [State] inside [task],
     * but use local variables and [MutableState.postValue] instead.
     */
    protected fun runOnDbThreadWithTransaction(
        readOnly: Boolean,
        task: (Transaction) -> Unit
    ) = briarExecutors.onDbThread {
        try {
            lifecycleManager.waitForDatabase()
            val txn = db.startTransaction(readOnly)
            try {
                task(txn)
                db.commitTransaction(txn)
            } finally {
                db.endTransaction(txn)
            }
        } catch (e: InterruptedException) {
            LOG.warn("Interrupted while waiting for database")
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            LOG.warn(e) { "Unhandled exception in database executor" }
        }
    }

    /**
     * Waits for the DB to open and runs the given task on the [DatabaseExecutor].
     *
     * All exceptions thrown inside the [DbRunnable] are passed to the [UiExecutor]
     * using the [onError] callback.
     */
    protected fun runOnDbThreadWithTransaction(
        readOnly: Boolean,
        task: DbRunnable<Exception>,
        @UiExecutor onError: (Exception) -> Unit
    ) {
        briarExecutors.onDbThread {
            try {
                lifecycleManager.waitForDatabase()
                db.transaction(readOnly, task)
            } catch (e: InterruptedException) {
                LOG.warn("Interrupted while waiting for database")
                Thread.currentThread().interrupt()
            } catch (e: Exception) {
                briarExecutors.onUiThread { onError(e) }
            }
        }
    }

    fun <T> MutableState<T>.postValue(value: T) = briarExecutors.onUiThread {
        this.value = value
    }

    fun <T> SnapshotStateList<T>.postUpdate(update: (SnapshotStateList<T>) -> Unit) = briarExecutors.onUiThread {
        update(this)
    }


    /**
     * Waits for the DB to open and runs the given task on the [DatabaseExecutor],
     * providing the result to the [onResult] callback in the UI thread.
     *
     * All exceptions thrown inside the [DbRunnable] are passed to the [UiExecutor]
     * using the [onError] callback.
     */
    protected fun <T> loadOnDbThreadWithTransaction(
        task: DbCallable<T, Exception>,
        @UiExecutor onResult: (T) -> Unit,
        @UiExecutor onError: (Exception) -> Unit
    ) {
        briarExecutors.onDbThread {
            try {
                lifecycleManager.waitForDatabase()
                db.transaction<Exception>(true) { txn ->
                    val t = task.call(txn)
                    txn.attach { onResult(t) }
                }
            } catch (e: InterruptedException) {
                LOG.warn("Interrupted while waiting for database")
                Thread.currentThread().interrupt()
            } catch (e: Exception) {
                briarExecutors.onUiThread { onError(e) }
            }
        }
    }
}
