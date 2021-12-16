import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.jetbrainsCompose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        mavenLocal()
        google()
        maven("https://plugins.gradle.org/m2/")
    }

    // keep version here in sync when updating briar
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("ru.vyarus:gradle-animalsniffer-plugin:1.5.3")
        classpath(files("briar/libs/gradle-witness.jar"))
    }

    // keep version here in sync when updating briar
    extra.apply {
        set("dagger_version", "2.33")
        set("okhttp_version", "3.12.13")
        set("tor_version", "0.3.5.17")
        set("obfs4proxy_version", "0.0.12-dev-40245c4a")
        set("junit_version", "4.13.2")
        set("jmock_version", "2.12.0")
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1-rc2"
    id("java")
    id("idea")
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

group = "app.briar.desktop"
version = "0.1"

allprojects {
    repositories {
        mavenCentral()
        jetbrainsCompose()
        google()
        jcenter()
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)

    implementation("com.github.ajalt.clikt:clikt:3.3.0")
    implementation("com.ibm.icu:icu4j:70.1")

    implementation(project(path = ":briar-core", configuration = "default"))
    implementation(project(path = ":bramble-java", configuration = "default"))

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")
    implementation("org.slf4j:jul-to-slf4j:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    val daggerVersion = "2.24"
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    testImplementation(kotlin("test-testng"))
    testImplementation("commons-io:commons-io:2.11.0")
    kaptTest("com.google.dagger:dagger-compiler:$daggerVersion")
}

tasks.test {
    useTestNG()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
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
            packageVersion = "0.0.1"
            description = "Secure messaging, anywhere"
            vendor = "The Briar Project"
            linux {
                packageName = "briar-desktop"
                iconFile.set(project.file("src/main/resources/images/logo_circle.png"))
                debMaintainer = "contact@briarproject.org"
                appCategory = "comm"
            }
            windows {
                iconFile.set(project.file("src/main/resources/images/logo_circle.ico"))
                upgradeUuid = "cc8b40f7-f190-4cea-bfec-ceb9ef85df09"
            }
        }
    }
}
