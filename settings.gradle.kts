pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "briar-desktop"

include(":bramble-api")
include(":bramble-core")
include(":bramble-java")
include(":briar-api")
include(":briar-core")
include(":desktop")
project(":bramble-api").projectDir = file("briar/bramble-api")
project(":bramble-core").projectDir = file("briar/bramble-core")
project(":bramble-java").projectDir = file("briar/bramble-java")
project(":briar-api").projectDir = file("briar/briar-api")
project(":briar-core").projectDir = file("briar/briar-core")
