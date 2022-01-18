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

package org.briarproject.briar.desktop.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.briar.desktop.ui.UiMode
import org.briarproject.briar.desktop.viewmodel.ViewModel
import javax.inject.Inject

class SidebarViewModel
@Inject
constructor(
    private val identityManager: IdentityManager,
) : ViewModel {

    override fun onInit() {
        loadAccountInfo()
    }

    private var _uiMode = mutableStateOf(UiMode.CONTACTS)
    private var _account = mutableStateOf<LocalAuthor?>(null)

    val uiMode: State<UiMode> = _uiMode
    val account: State<LocalAuthor?> = _account

    fun setUiMode(uiMode: UiMode) {
        _uiMode.value = uiMode
    }

    fun loadAccountInfo() {
        _account.value = identityManager.localAuthor
    }
}
