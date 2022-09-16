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

package org.briarproject.briar.desktop.settings

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DesktopSettingsModule(
    private val unencryptedSettingsPostfix: String? = null,
) {
    @Provides
    @Singleton
    fun provideUnencryptedSettings(): UnencryptedSettings = UnencryptedSettingsImpl(unencryptedSettingsPostfix)

    @Provides
    @Singleton
    // provide [UnencryptedSettings] singleton itself as provided above to use same object
    fun provideUnencryptedSettingsReadOnly(settings: UnencryptedSettings): UnencryptedSettingsReadOnly = settings

    @Provides
    @Singleton
    fun provideEncryptedSettings(settings: EncryptedSettingsImpl): EncryptedSettings = settings

    @Provides
    @Singleton
    // provide [EncryptedSettings] singleton itself as provided above to use same object
    fun provideEncryptedSettingsReadOnly(settings: EncryptedSettings): EncryptedSettingsReadOnly = settings
}
