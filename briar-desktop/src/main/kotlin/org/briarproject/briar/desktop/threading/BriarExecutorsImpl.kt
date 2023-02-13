/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.threading

import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.DbCallable
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BriarExecutorsImpl
@Inject
constructor(
    @UiExecutor private val uiExecutor: Executor,
    @DatabaseExecutor private val dbExecutor: Executor,
    @IoExecutor private val ioExecutor: Executor,
    private val lifecycleManager: LifecycleManager,
    private val db: TransactionManager,
) : BriarExecutors {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    override fun onDbThread(@DatabaseExecutor task: () -> Unit) = dbExecutor.execute {
        try {
            lifecycleManager.waitForDatabase()
            task()
        } catch (e: InterruptedException) {
            LOG.w { "Interrupted while waiting for database" }
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            LOG.w(e) { "Unhandled exception in database executor" }
        }
    }

    override suspend fun <T> runOnDbThread(@DatabaseExecutor task: () -> T) =
        suspendCoroutine { cont ->
            // The coroutine suspends until the DatabaseExecutor has finished the task
            // and ended the transaction. It then resumes with the returned value.
            onDbThread {
                val t = task()
                cont.resume(t)
            }
        }

    override fun onDbThreadWithTransaction(
        readOnly: Boolean,
        @DatabaseExecutor task: (Transaction) -> Unit,
    ) = dbExecutor.execute {
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
            LOG.w { "Interrupted while waiting for database" }
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            LOG.w(e) { "Unhandled exception in database executor" }
        }
    }

    override suspend fun <T> runOnDbThreadWithTransaction(
        readOnly: Boolean,
        @DatabaseExecutor task: (Transaction) -> T,
    ) = suspendCoroutine<T> { cont ->
        // The coroutine suspends until the DatabaseExecutor has finished the task
        // and ended the transaction. It then resumes with the returned value.
        onDbThread {
            val t = db.transactionWithResult(readOnly, DbCallable { txn -> task(txn) })
            cont.resume(t)
        }
    }

    override fun onUiThread(@UiExecutor task: () -> Unit) = uiExecutor.execute(task)

    override fun onIoThread(@IoExecutor task: () -> Unit) = ioExecutor.execute(task)
}
