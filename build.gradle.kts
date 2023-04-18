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
        classpath("ru.vyarus:gradle-animalsniffer-plugin:1.5.3")
        classpath(files("briar/libs/gradle-witness.jar"))
    }

    // keep version here in sync when updating briar
    extra.apply {
        set("kotlin_version", "1.7.10") // todo: different version used in Briar-Desktop?!
        set("dagger_version", "2.43.2")
        set("okhttp_version", "3.12.13")
        set("jackson_version", "2.13.4")
        set("tor_version", "0.4.7.13-2")
        set("obfs4proxy_version", "0.0.14-tor2")
        set("snowflake_version", "2.5.1")
        set("jsoup_version", "1.15.3")
        set("bouncy_castle_version", "1.71")
        set("junit_version", "4.13.2")
        set("jmock_version", "2.12.0")
        set("mockwebserver_version", "4.9.3")
        set("onionwrapper_version", "0.0.1")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
}
