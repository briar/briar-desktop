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

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
        maven("https://plugins.gradle.org/m2/")
    }

    // keep version here in sync when updating briar
    dependencies {
        classpath("ru.vyarus:gradle-animalsniffer-plugin:2.0.1")
        classpath(files("briar/libs/gradle-witness.jar"))
    }

    // keep version here in sync when updating briar
    extra.apply {
        // when updating kotlin_version, also update briar-desktop/build.gradle.kts kotlin("jvm") and kotlin("kapt")
        set("kotlin_version", "2.1.10") // mind the comment above!
        set("dagger_version", "2.51.1")
        set("okhttp_version", "4.12.0")
        set("jackson_version", "2.13.4")
        set("tor_version", "0.4.9.11")
        set("lyrebird_version", "0.6.2")
        set("jsoup_version", "1.15.3")
        set("bouncy_castle_version", "1.82")
        set("junit_version", "4.13.2")
        set("jmock_version", "2.12.0")
        set("mockwebserver_version", "4.10.0")
        set("onionwrapper_version", "0.1.6")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
