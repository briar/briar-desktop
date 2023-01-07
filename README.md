# Briar Desktop

[![pipeline status](https://code.briarproject.org/briar/briar-desktop/badges/main/pipeline.svg)](https://code.briarproject.org/briar/briar-desktop/commits/main)
[![coverage report](https://code.briarproject.org/briar/briar-desktop/badges/main/coverage.svg)](https://code.briarproject.org/briar/briar-desktop/commits/main)

A desktop program for [Briar](https://briar.app), bringing secure messaging to your desktop and mobile devices.

![Screenshot showing private chat with a test contact 'Polonius', containing a text by Shakespeare](/utils/screenshots/briar-desktop-1.png)

## Installation

We plan to ship Briar as Flatpak and in Debian (and related distributions) as _.deb_,
but until now, there are only semi-official installation methods available. You might want to use them now
in order to not having to wait, but please note that those installation methods will be deprecated
once Briar is officially available.

### Beta releases

From Briar's website you can download different binaries:
[briarproject.org/download-briar-desktop](https://briarproject.org/download-briar-desktop/)

### Nightly builds

Each night a new build is compiled by Briar's GitLab CI.

#### Self-contained Java .jar

The simplest way is to download the nightly
[briar-desktop-linux.jar](https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-linux.jar?job=b_package_linux)
or
[briar-desktop-windows.jar](https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-windows.jar?job=b_package_windows)
and execute it from the command-line with
`java -jar briar-desktop-linux.jar` or `java -jar briar-desktop-windows.jar` respectively.
Note that you need at least version 17 of the Java Runtime Environment.

#### Self-contained .deb

For Debian- and Ubuntu-based Linux distributions, a set of .deb files is available.
Depending on which version of Debian/Ubuntu your OS is based on,
you can choose the right .deb file:
* [Ubuntu 20.04](https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-ubuntu-20.04.deb?job=b_package_linux)
* [Debian stable (bullseye)](https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-debian-bullseye.deb?job=b_package_linux)
* [Ubuntu 18.04](https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-ubuntu-18.04.deb?job=b_package_linux)

Here are some examples of popular distributions and their respective .deb file:
* Ubuntu 20.04 and compatible: Linux Mint 20.X (Ulyana, Ulyssa, Uma, Una), elementaryOS 6.X (Odin, Jólnir), Trisquel 10 (Nabia)
* Debian stable (bullseye) and compatible: MX Linux MX-21
* Ubuntu 18.04 and compatible: Linux Mint 19.X (Tara, Tessa, Tina, Tricia), elementaryOS 5.X (Juno, Hera), Trisquel 9 (Etiona)

#### Windows installer .msi

For Windows, an .msi installer package is available:
* [Windows MSI](https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/Briar-Desktop.msi?job=b_package_windows)

## Developers

### Download Source Code

Briar dependencies are included as [Git Submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules).
To be able to build Briar Desktop, download the source code and the submodules using

```shell
git clone --recurse-submodules git@code.briarproject.org:briar/briar-desktop.git
```

or

```shell
git clone --recurse-submodules https://code.briarproject.org/briar/briar-desktop.git
```

### Intellij IDEA

The easiest and most convenient way to build Briar Desktop is by using
[Intellij IDEA](https://www.jetbrains.com/idea/).

### UI Previews

Briar Desktop makes use of [Compose for Desktop](https://www.jetbrains.com/lp/compose/)
to build its UI. The Intellij IDEA plugin
[Compose Multiplatform IDE Support](https://plugins.jetbrains.com/plugin/16541-compose-multiplatform-ide-support)
provides static previews of
composable functions without parameters which are annotated with `@Preview`.

### Building and Running

In order to build and run the application from the command line, execute this:

    ./gradlew pinpitRun

You can specify arguments to the app using the `--args` option of the
Gradle task. For example to show the usage info:

    ./gradlew pinpitRun --args="--help"

To specify a different data directory and enable the debug output:

    ./gradlew pinpitRun --args="--debug --data-dir=/tmp/briar-tmp"

In case you want to build a self-contained Debian packages and an MSI installer,
note that you need at least JDK 17:

    ./gradlew pinpitPackageDefault

If you want to use the JDKs downloaded by Intellij IDEA, you can for example specify the path like this:

    ./gradlew pinpitPackageDefault -Dorg.gradle.java.home=$HOME/.jdks/openjdk-17.0.2

Take a look at [_.gitlab-ci.yml_](.gitlab-ci.yml) if you have problems with Kotlin and JDK 17.

## Translations

See [TRANSLATION.md](./TRANSLATION.md) for more information.

## Hacking

See [HACKING.md](./HACKING.md) for useful information when trying to work
with the source code.

## Design Goals

* Intuitive UI, similar to Briar Android client
* Main platform is GNU/Linux, but also support (at least) Windows and macOS
* Analogously, main platform is x86, but also support (at least) arm
* Adaptive to different screen sizes (desktop and mobile devices)
* Has [phone constraints](https://developer.puri.sm/Librem5/Apps/Guides/Design/Constraints.html) in mind

## FAQ

### How can I delete my account?

When restarting Briar Desktop you can select "I have forgotten my password"
in order to delete your account.

## License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
[GNU Affero General Public License](LICENSE.md) for more details.
