import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
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
        set("junit_version", "4.13.2")
        set("jmock_version", "2.12.0")
    }
}

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("kapt") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    id("java")
    id("idea")
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

group = "app.briar.desktop"
version = "0.1"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        jcenter()
    }
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.0")
    implementation("com.github.ajalt:clikt:2.2.0")
    implementation("org.jetbrains.compose.material:material-icons-extended:0.4.0")
    implementation("com.ibm.icu:icu4j:70.1")

    implementation(project(path = ":briar-core", configuration = "default"))
    implementation(project(path = ":bramble-java", configuration = "default"))

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
