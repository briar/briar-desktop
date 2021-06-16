import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.3.72"
    id("org.jetbrains.compose") version "0.4.0"
    id("java")
    id("idea")
}

group = "app.briar.desktop"
version = "0.1"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.0")
    implementation("com.github.ajalt:clikt:2.2.0")

    implementation(project(path = ":briar:briar-core", configuration = "default"))
    implementation(project(path = ":briar:bramble-java", configuration = "default"))

    val daggerVersion = "2.24"
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    testImplementation(kotlin("test-testng"))
}

tasks.test {
    useTestNG()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "app.briar.desktop"
            packageVersion = "1.0.0"
        }
    }
}
