package org.briarproject.briar.desktop.paul.data

import org.briarproject.briar.desktop.paul.model.ContentType

object ContentTypeList {
    val types = listOf(
        ContentType(id = -1, name = "Profile"),
        ContentType(id = 0, name = "Contacts"),
        ContentType(id = 1, name = "Private Groups"),
        ContentType(id = 2, name = "Forums"),
        ContentType(id = 3, name = "Blogs"),
        ContentType(id = 4, name = "Transports"),
        ContentType(id = 5, name = "Settings"),
        ContentType(id = 6, name = "Sign Out")
    )
}
