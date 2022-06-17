#!/bin/bash

# A script for extracting the deb artifact to a local directory for
# debugging its content.

set -e

DIR=$(dirname $0)
REPO="$DIR/.."
OUTPUT="$REPO/deb"

echo "Creating output directory"
mkdir -p "$OUTPUT"

echo "Extracting debian archive"
ar --output "$OUTPUT" x "$REPO/briar-desktop/build/compose/binaries/main/deb"/briar-desktop*.deb

echo "Extracting control.tar.xz"
mkdir -p "$OUTPUT/control"
tar xv --directory "$OUTPUT/control" -f "$OUTPUT/control.tar.xz"

echo "Extracting data.tar.xz"
mkdir -p "$OUTPUT/data"
tar xv --directory "$OUTPUT/data" -f "$OUTPUT/data.tar.xz"
