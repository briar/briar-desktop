pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}
rootProject.name = "briar-desktop"

include("briar:bramble-api")
include("briar:bramble-core")
include("briar:bramble-java")
include("briar:briar-api")
include("briar:briar-core")
