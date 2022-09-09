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

import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.settings.Settings
import org.briarproject.bramble.api.settings.SettingsManager
import javax.inject.Inject

const val SETTINGS_NAMESPACE = "desktop-ui" // NON-NLS

const val PREF_NOTIFY_VISUAL = "notify_visual" // NON-NLS
const val PREF_NOTIFY_SOUND = "notify_sound" // NON-NLS

class EncryptedSettingsImpl
@Inject internal constructor(
    private val settingsManager: SettingsManager,
    lifecycleManager: LifecycleManager,
) : EncryptedSettings {

    init {
        lifecycleManager.registerOpenDatabaseHook { txn ->
            settings = settingsManager.getSettings(txn, SETTINGS_NAMESPACE)
        }
    }

    private lateinit var settings: Settings

    override var visualNotifications: Boolean
        get() = settings.getBoolean(PREF_NOTIFY_VISUAL, true)
        @IoExecutor
        set(value) {
            settings.putBoolean(PREF_NOTIFY_VISUAL, value)
            settingsManager.mergeSettings(settings, SETTINGS_NAMESPACE)
        }

    override var soundNotifications: Boolean
        get() = settings.getBoolean(PREF_NOTIFY_SOUND, true)
        @IoExecutor
        set(value) {
            settings.putBoolean(PREF_NOTIFY_SOUND, value)
            settingsManager.mergeSettings(settings, SETTINGS_NAMESPACE)
        }
}
