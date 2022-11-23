/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("HardCodedStringLiteral")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("kapt") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.1"
    id("java")
    id("idea")
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("org.briarproject.briar.desktop.build-data-gradle-plugin")
}

val versionCode = "0.3.1"
val buildType = if (project.hasProperty("buildType")) project.properties["buildType"] else "snapshot"
group = "app.briar.desktop"
version = "$versionCode-$buildType"

buildData {
    packageName = "org.briarproject.briar.desktop"
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    // needed to access Dispatchers.Swing for EventExecutor
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    implementation("com.ibm.icu:icu4j:70.1")
    implementation("net.java.dev.jna:jna:5.12.1")

    implementation(project(path = ":briar-core", configuration = "default"))
    implementation(project(path = ":bramble-java", configuration = "default"))

    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("org.slf4j:jul-to-slf4j:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    val daggerVersion = "2.24"
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    testImplementation(kotlin("test-testng"))
    testImplementation(project(path = ":bramble-core", configuration = "testOutput"))
    testImplementation("commons-io:commons-io:2.11.0")
    kaptTest("com.google.dagger:dagger-compiler:$daggerVersion")
}

// hacky fix for upstream issue when selecting skiko in gradle
// see https://github.com/JetBrains/skiko/issues/547
// and https://github.com/JetBrains/compose-jb/issues/1404
configurations.all {
    attributes {
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

tasks.test {
    useTestNG()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    exclude("META-INF/BC1024KE.RSA", "META-INF/BC1024KE.SF", "META-INF/BC1024KE.DSA")
    exclude("META-INF/BC2048KE.RSA", "META-INF/BC2048KE.SF", "META-INF/BC2048KE.DSA")
}

tasks.jar {
    exclude("META-INF/BC1024KE.RSA", "META-INF/BC1024KE.SF", "META-INF/BC1024KE.DSA")
    exclude("META-INF/BC2048KE.RSA", "META-INF/BC2048KE.SF", "META-INF/BC2048KE.DSA")
}

compose.desktop {
    application {
        mainClass = "org.briarproject.briar.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "Briar"
            description = "Secure messaging, anywhere"
            vendor = "The Briar Project"
            copyright = "2021-2022 The Briar Project"
            licenseFile.set(project.file("LICENSE.txt"))
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/appResources"))
            // As described at https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#configuring-included-jdk-modules
            // the Gradle plugin does not automatically determine necessary JDK modules to ship
            // so that we need to define required modules here:
            modules("java.sql")
            modules("java.naming")
            modules("jdk.localedata")
            linux {
                packageName = "briar-desktop"
                // Explicitly specifying the debian revision '-1' doesn't seem to work, it gets always appended.
                // I think we're fine having revision '-1' as it will only be used to break ties when the upstream
                // version is the same for two packages.
                debPackageVersion = "$versionCode-$buildType"
                // rpm versions may not contain hyphens, so use underscore
                rpmPackageVersion = "${versionCode}_$buildType"
                iconFile.set(project.file("src/main/resources/images/logo_circle.png"))
                debMaintainer = "contact@briarproject.org"
                appCategory = "comm"
                menuGroup = "Network;Chat;InstantMessaging;"
            }
            windows {
                iconFile.set(project.file("src/main/resources/images/logo_circle.ico"))
                upgradeUuid = "cc8b40f7-f190-4cea-bfec-ceb9ef85df09"
                // Windows doesn't support things like 'nightly' or 'release'. Only numeric versions are acceptable
                packageVersion = versionCode
            }
        }
    }
}

tasks.create<Jar>("notificationTest") {
    dependsOn.addAll(
        listOf(
            "compileJava",
            "compileKotlin",
            "processResources"
        )
    ) // We need this for Gradle optimization to work
    archiveFileName.set("notificationTest.jar") // Version-agnostic name of the jar
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to "org.briarproject.briar.desktop.notification.linux.TestNativeNotificationsKt")
    } // Provided we set it up in the application plugin configuration
    val sourcesTest = sourceSets.test.get()
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) }
    from(contents)
    // add normal jar outputs
    with(tasks["jar"] as CopySpec)
    // add testing output, too
    from(sourceSets["test"].output)
    from(sourceSets["main"].output)
}
