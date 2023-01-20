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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("kapt") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.1"
    id("de.mobanisto.pinpit") version "0.3.0"
    id("java")
    id("idea")
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("org.briarproject.briar.desktop.build-data-gradle-plugin")
}

// We need to create separate Gradle configurations for Pinpit here. For each supported
// target platform (OS+arch) we need one configuration in order to set the right native
// Compose  dependency on it in the dependencies {} stanza below. The names need to be
// exactly like this because Pinpit currently uses hardcoded configuration names when
// deciding which configuration to use for packaging for each target platform.

val currentOs: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

val windowsX64: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

val linuxX64: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

sourceSets {
    main {
        java {
            compileClasspath = currentOs
            runtimeClasspath = currentOs
        }
    }
    test {
        java {
            compileClasspath += currentOs
            runtimeClasspath += currentOs
        }
    }
}

val versionCode = "0.3.1"
val buildType = if (project.hasProperty("buildType")) project.properties["buildType"] else "snapshot"
group = "app.briar.desktop"
version = "$versionCode-$buildType"
val appVendor = "The Briar Project"
val appName = "Briar"
val appDescription = "Secure messaging, anywhere"

buildData {
    packageName = "org.briarproject.briar.desktop"
    windowsAumi = appName
}

dependencies {
    currentOs(compose.desktop.currentOs)
    windowsX64(compose.desktop.windows_x64)
    linuxX64(compose.desktop.linux_x64)

    implementation(compose.materialIconsExtended)
    // needed to access Dispatchers.Swing for EventExecutor
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    implementation("com.ibm.icu:icu4j:70.1")
    implementation("net.java.dev.jna:jna:5.12.1")

    implementation(project(path = ":briar-core", configuration = "default"))
    implementation(project(path = ":bramble-java", configuration = "default"))

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:jul-to-slf4j:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("de.mobanisto:toast4j:0.2.0")

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

pinpit.desktop {
    application {
        mainClass = "org.briarproject.briar.desktop.MainKt"
        nativeDistributions {
            jvmVendor = "adoptium"
            jvmVersion = "17.0.5+8"

            packageName = appName
            packageVersion = version.toString()
            description = appDescription
            vendor = appVendor
            copyright = "2021-2023 $appVendor"
            licenseFile.set(project.file("LICENSE.txt"))
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/appResources"))
            // The required JDK modules to ship along with the distribution cannot easily
            // be determined automatically, so it's necessary to specify them manually here:
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
                debPreInst.set(project.file("src/packagingResources/linux/deb/preinst"))
                debPostInst.set(project.file("src/packagingResources/linux/deb/postinst"))
                debPreRm.set(project.file("src/packagingResources/linux/deb/prerm"))
                debCopyright.set(project.file("src/packagingResources/linux/deb/copyright"))
                debLauncher.set(project.file("src/packagingResources/linux/launcher.desktop"))
                debMaintainer = "contact@briarproject.org"
                appCategory = "comm"
                // The packages mentioned in the depends() call can be found by running
                // `./gradlew pinpitSuggestDebDependencies` on the respective target system.
                // This uses a combination of `ldd` and `dpkg -S` on each `.so` file shipped with the app
                // to find out which other shared libraries this depends on and which Debian package
                // provides it.
                deb("UbuntuJammyX64") {
                    qualifier = "ubuntu-22.04"
                    arch = "x64"
                    depends(
                        // determined by pinpitSuggestDebDependencies
                        "libc6", "libexpat1", "zlib1g",
                        // manually added
                        "xdg-utils", "libnotify4"
                    )
                }
                deb("UbuntuFocalX64") {
                    qualifier = "ubuntu-20.04"
                    arch = "x64"
                    depends(
                        // determined by pinpitSuggestDebDependencies
                        "libc6", "libexpat1", "libuuid1", "zlib1g",
                        // manually added
                        "xdg-utils", "libnotify4"
                    )
                }
                deb("UbuntuBionicX64") {
                    qualifier = "ubuntu-18.04"
                    arch = "x64"
                    depends(
                        // determined by pinpitSuggestDebDependencies
                        "libasound2", "libbsd0", "libc6", "libexpat1", "libfontconfig1",
                        "libfreetype6", "libgl1", "libglvnd0", "libglx0", "libpng16-16",
                        "libx11-6", "libxau6", "libxcb1", "libxdmcp6", "libxext6",
                        "libxi6", "libxrender1", "libxtst6", "zlib1g",
                        // manually added
                        "xdg-utils", "libnotify4"
                    )
                }
                deb("DebianBullseyeX64") {
                    qualifier = "debian-bullseye"
                    arch = "x64"
                    depends(
                        // determined by pinpitSuggestDebDependencies
                        "libc6", "libexpat1", "zlib1g",
                        // manually added
                        "xdg-utils", "libnotify4"
                    )
                }
            }
            windows {
                dirChooser = true
                shortcut = true
                iconFile.set(project.file("src/main/resources/images/logo_circle.ico"))
                aumid = appName
                upgradeUuid = "cc8b40f7-f190-4cea-bfec-ceb9ef85df09"
                // Windows doesn't support things like 'nightly' or 'release'. Only numeric versions are acceptable
                packageVersion = versionCode
                mainExeFileDescription = "$appName - $appDescription"
                msi {
                    arch = "x64"
                    bitmapBanner.set(project.file("src/packagingResources/windows/banner.bmp"))
                    bitmapDialog.set(project.file("src/packagingResources/windows/dialog.bmp"))
                }
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
