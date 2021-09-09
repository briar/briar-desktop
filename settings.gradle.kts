pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
}

include(":desktop")
include(":bramble-api")
include(":bramble-core")
include(":bramble-java")
include(":briar-api")
include(":briar-core")
