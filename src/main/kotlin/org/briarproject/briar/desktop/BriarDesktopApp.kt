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

package org.briarproject.briar.desktop

import dagger.Component
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.BrambleCoreModule
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.BriarCoreModule
import org.briarproject.briar.desktop.ui.BriarUi
import java.security.SecureRandom
import javax.inject.Singleton

@Component(
    modules = [
        BrambleCoreModule::class,
        BriarCoreModule::class,
        DesktopModule::class
    ]
)
@Singleton
internal interface BriarDesktopApp : BrambleCoreEagerSingletons, BriarCoreEagerSingletons {

    fun getBriarUi(): BriarUi

    fun getSecureRandom(): SecureRandom
}
