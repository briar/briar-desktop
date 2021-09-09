# Briar Desktop

[![pipeline status](https://code.briarproject.org/briar/briar-desktop/badges/main/pipeline.svg)](https://code.briarproject.org/briar/briar-desktop/commits/main)
[![coverage report](https://code.briarproject.org/briar/briar-desktop/badges/main/coverage.svg)](https://code.briarproject.org/briar/briar-desktop/commits/main)

A desktop program for [Briar](https://briar.app), bringing secure messaging to your desktop and mobile devices.

**Note that Briar Desktop is still in a very early prototype stage and not yet functional.**

For regular updates, check out the Briar tag on
[Nico Alt's blog](https://nico.dorfbrunnen.eu/tags/briar/)
([RSS feed](https://nico.dorfbrunnen.eu/tags/briar/index.xml))
and watch the
[milestones of this repo](https://code.briarproject.org/briar/briar-desktop/-/milestones).

## Installation

We plan to distribute Briar for Debian (and related distributions) as _.deb_ and
via [Flathub.org](https://flathub.org) as flatpak, but until now, there are
only semi-official installation methods available. You might want to use them now
for not having to wait, but please note that those installation methods will be deprecated
once Briar is available for Debian and on Flathub.org.

### Self-contained Java .jar

The simplest way is to download the nightly briar-desktop.jar and execute it from the command-line.

**Note that this installation method isn't yet available.**

```
wget https://media.dorfbrunnen.eu/briar/desktop/briar-desktop.jar
java -jar briar-desktop.jar
```

## Developers

### Download Source Code

Briar dependencies are included as [Git Submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules).
To be able to build Briar Desktop, download the source code and the submodules using

```shell
git clone --recursive-submodules git@code.briarproject.org:briar/briar-desktop.git
```

or

```shell
git clone --recursive-submodules https://code.briarproject.org/briar/briar-desktop.git
```

### Intellij IDEA

The easiest and most convenient way to build Briar Desktop is by using
[Intellij IDEA](https://www.jetbrains.com/idea/).

## Translations

See [TRANSLATION.md](./TRANSLATION.md) for more information.

## Maintenance

From time to time, translations and Flatpak's dependencies should be
updated. The former can be done with
`tools/update-translations.sh`, the latter using
[flatpak-builder-tools](https://github.com/flatpak/flatpak-builder-tools).

## Design Goals

* Intuitive UI, similar to Briar Android client
* Main platform is GNU/Linux, but also support (at least) Windows and macOS
* Analogously, main platform is x86, but also support (at least) arm
* Adaptive to different screen sizes (desktop and mobile devices)
* Has [phone constraints](https://developer.puri.sm/Librem5/Apps/Guides/Design/Constraints.html) in mind

## License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
[GNU Affero General Public License](LICENSE.md) for more details.
