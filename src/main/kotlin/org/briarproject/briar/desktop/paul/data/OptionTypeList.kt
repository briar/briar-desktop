package org.briarproject.briar.desktop.paul.data

import org.briarproject.briar.desktop.paul.model.OptionType

object OptionTypeList {
    val msgTypes = listOf(
        OptionType(name = "Alice", unread = 2, online = true),
        OptionType(name = "Bob", unread = 0, online = true),
        OptionType(name = "Carl", unread = 0, online = false),
        OptionType(name = "Dan", unread = 1, online = false),
        OptionType(name = "Eve", unread = 0, online = false),
    )
}
