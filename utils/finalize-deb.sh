#!/bin/bash

# A script for manually adding files to the produced .deb files.
# https://unix.stackexchange.com/a/138190

set -ex

DIR=$(dirname $0)
REPO="$DIR/.."
OUTPUT="$REPO/deb"

cd "$REPO/build/compose/binaries/main/deb"

mkdir tmp
cd tmp

# Unpack control.tar.xz to tmp directory
ar p ../*.deb control.tar.xz | tar -xJ
ar d ../*.deb control.tar.xz

# Replace preinst, postinst, prerm scripts
#TODO

# Repackage briar-desktop.deb
cp ../*.deb ../briar-desktop.deb
tar cfJ control.tar.xz ./*[!z]
ar r ../briar-desktop.deb control.tar.xz
