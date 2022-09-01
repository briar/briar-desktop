#!/bin/bash

# A script for manually adding files to the produced .deb files.
# https://unix.stackexchange.com/a/138190

set -ex

DIR=$(dirname $0)
REPO="$DIR/.."

cd "$REPO/briar-desktop/build/compose/binaries/main/deb"

cp ./briar-desktop_*.deb briar-desktop.original.deb

fakeroot sh -c '
  mkdir tmp
  dpkg-deb -R briar-desktop.original.deb tmp
  cp ../../../../../src/packagingResources/linux/postinst tmp/DEBIAN/postinst
  cp ../../../../../src/packagingResources/linux/preinst tmp/DEBIAN/preinst
  cp ../../../../../src/packagingResources/linux/prerm tmp/DEBIAN/prerm
  sed -i "/Depends:/s/$/, libnotify4/" tmp/DEBIAN/control
  dpkg-deb -b tmp briar-desktop.deb
  rm -r tmp
'
