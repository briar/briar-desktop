package org.briarproject.briar.desktop.builddata

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

open class BuildDataPluginExtension {
    @Input
    var packageName: String? = null

    @Input
    @Optional
    var className: String? = null
}
