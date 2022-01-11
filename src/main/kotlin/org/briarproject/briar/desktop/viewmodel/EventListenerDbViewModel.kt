/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
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

package org.briarproject.briar.desktop.viewmodel

import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.threading.BriarExecutors

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
