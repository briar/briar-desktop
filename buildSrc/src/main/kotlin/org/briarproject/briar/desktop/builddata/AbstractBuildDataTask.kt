package org.briarproject.briar.desktop.builddata

import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Nested

abstract class AbstractBuildDataTask : ConventionTask() {

    @Nested
    var configuration: BuildDataPluginExtension? = null
}
