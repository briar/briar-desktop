/*
 * Copyright 2016 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.briarproject.briar.desktop.notification.windows

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.win32.W32APIOptions
import org.briarproject.bramble.util.OsUtils
import kotlin.collections.Map.Entry

internal object JnaHelper {

    fun register(libraryName: String, clazz: Class<*>): NativeLibrary {
        val options: MutableMap<String, Any> = HashMap()
        options[Library.OPTION_CLASSLOADER] = clazz.classLoader
        if (OsUtils.isWindows()) {
            val entries: Set<Entry<String, Any>> = W32APIOptions.DEFAULT_OPTIONS.entries
            for ((key, value) in entries) {
                options[key] = value
            }
        }
        val library: NativeLibrary = NativeLibrary.getInstance(libraryName, options)
            ?: throw IllegalArgumentException("$libraryName doesn't exist or cannot be loaded.")
        Native.register(clazz, library)
        return library
    }
}
