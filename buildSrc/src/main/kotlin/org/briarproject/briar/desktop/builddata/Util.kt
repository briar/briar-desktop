package org.briarproject.briar.desktop.builddata

import java.nio.file.Path

internal object Util {
    fun getSourceDir(pathBuildDir: Path): Path {
        return pathBuildDir.resolve("generatedBuildData")
    }
}
