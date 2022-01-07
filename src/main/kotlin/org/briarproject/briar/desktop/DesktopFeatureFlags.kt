package org.briarproject.briar.desktop

interface DesktopFeatureFlags {

    fun shouldEnablePrivateGroups(): Boolean

    fun shouldEnableForums(): Boolean

    fun shouldEnableBlogs(): Boolean

    fun shouldEnableTransportSettings(): Boolean
}
