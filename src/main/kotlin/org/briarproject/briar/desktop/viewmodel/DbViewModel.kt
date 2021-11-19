package org.briarproject.briar.desktop.viewmodel

import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.DbCallable
import org.briarproject.bramble.api.db.DbException
import org.briarproject.bramble.api.db.DbRunnable
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
     * Waits for the DB to open and runs the given task on the [DatabaseExecutor].
     *
     * The [Runnable] has to handle all potential exceptions, e.g. [DbException]s,
     * in a thread-safe way itself.
     * For convenience, consider using [runOnDbThreadWithTransaction] instead.
     */
    protected fun runOnDbThread(task: Runnable) {
        briarExecutors.onDbThread {
            try {
                lifecycleManager.waitForDatabase()
                task.run()
            } catch (e: InterruptedException) {
                LOG.warn("Interrupted while waiting for database")
                Thread.currentThread().interrupt()
            }
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
