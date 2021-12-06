package org.briarproject.briar.desktop.viewmodel

import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.threading.BriarExecutors

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
     * To avoid inconsistent state between the database and the UI
     * whenever the UI should react to a successful transaction,
     * strongly consider using [runOnDbThreadWithTransaction] instead.
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
     * Updates to the UI that depend on a successful transaction in the database
     * should be attached to the transaction via [Transaction.attach].
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
}
