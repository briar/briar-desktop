/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Language
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.prefs.Preferences

@Suppress("HardCodedStringLiteral")
class UnencryptedSettingsImplTest {

    // Unfortunately, this test is not side effect free: it does update the preferences.
    // In order to reset value to what was stored in the preferences node before running
    // the test, remember the value during @Before and reset it to that value during @After.

    private val prefs: Preferences = Preferences.userNodeForPackage(UnencryptedSettingsImpl::class.java)
    private var oldValue: String? = null

    @Before
    fun rememberValuesBeforeTest() {
        oldValue = prefs.get(PREF_LANG, null)
    }

    @After
    fun restoreValuesAfterTest() {
        if (oldValue == null) {
            prefs.remove(PREF_LANG)
        } else {
            prefs.put(PREF_LANG, oldValue)
        }
    }

    @Test
    fun testNoKey() {
        prefs.remove(PREF_LANG)
        val settings = UnencryptedSettingsImpl()
        assertEquals(Language.DEFAULT, settings.language)
    }

    @Test
    fun testValidKey() {
        prefs.put(PREF_LANG, "FR")
        val settings = UnencryptedSettingsImpl()
        assertEquals(Language.FR, settings.language)
    }

    @Test
    fun testInvalidKey() {
        val logger = LoggerFactory.getLogger(UnencryptedSettingsImpl::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>().apply { start() }
        logger.addAppender(appender)

        // put an invalid key into the preference store
        prefs.put(PREF_LANG, "foo")

        // expect the default value to be returned by the settings implementation
        val settings = UnencryptedSettingsImpl()
        assertEquals(Language.DEFAULT, settings.language)

        // and also expect it to log an error message
        assertEquals(1, appender.list.size)
        val event = appender.list[0]
        assertEquals(Level.ERROR, event.level)
        assert(event.message.contains("Unexpected enum value"))
    }
}
