/*
 * Copyright 2017 dorkbox, llc
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

import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND

/**
 * http://msdn.microsoft.com/en-us/library/windows/desktop/bb773352(v=vs.85).aspx
 */
internal class NOTIFYICONDATA : Structure() {
    companion object {
        const val NIF_MESSAGE = 0x1
        const val NIF_ICON = 0x2
        const val NIF_TIP = 0x4
        const val NIF_STATE = 0x8
        const val NIF_INFO = 0x10
        const val NIIF_NONE = 0x0
        const val NIIF_INFO = 0x1
        const val NIIF_WARNING = 0x2
        const val NIIF_ERROR = 0x3
        const val NIIF_USER = 0x4
        const val NIIF_NOSOUND = 0x10
    }

    @JvmField
    var cbSize: Int = 0 // cannot use size() directly here, will throw IllegalStateException

    @JvmField
    var hWnd: HWND? = null

    @JvmField
    var uID = 0

    @JvmField
    var uFlags = 0

    @JvmField
    var uCallbackMessage = 0

    @JvmField
    var hIcon: WinDef.HICON? = null

    @JvmField
    var szTip = CharArray(128)

    @JvmField
    var dwState = 0

    @JvmField
    var dwStateMask = 0

    @JvmField
    var szInfo = CharArray(256)

    @JvmField
    var uTimeoutOrVersion = 0 // {UINT uTimeout; UINT uVersion;};

    @JvmField
    var szInfoTitle = CharArray(64)

    @JvmField
    var dwInfoFlags = 0

    init {
        cbSize = size()
    }

    fun setTooltip(s: String) {
        uFlags = uFlags or NIF_TIP
        System.arraycopy(s.toCharArray(), 0, szTip, 0, Math.min(s.length, szTip.size))
        szTip[s.length] = '\u0000'
    }

    fun setBalloon(title: String, message: String, millis: Int, niif: Int) {
        uFlags = uFlags or NIF_INFO
        System.arraycopy(message.toCharArray(), 0, szInfo, 0, Math.min(message.length, szInfo.size))
        szInfo[message.length] = '\u0000'
        uTimeoutOrVersion = millis
        System.arraycopy(title.toCharArray(), 0, szInfoTitle, 0, Math.min(title.length, szInfoTitle.size))
        szInfoTitle[title.length] = '\u0000'
        dwInfoFlags = niif
    }

    fun setIcon(hIcon: WinDef.HICON?) {
        uFlags = uFlags or NIF_ICON
        this.hIcon = hIcon
    }

    fun setCallback(callback: Int) {
        uFlags = uFlags or NIF_MESSAGE
        uCallbackMessage = callback
    }

    override fun getFieldOrder(): List<String> {
        return listOf(
            "cbSize",
            "hWnd",
            "uID",
            "uFlags",
            "uCallbackMessage",
            "hIcon",
            "szTip",
            "dwState",
            "dwStateMask",
            "szInfo",
            "uTimeoutOrVersion",
            "szInfoTitle",
            "dwInfoFlags"
        )
    }
}
