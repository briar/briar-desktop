#!/bin/bash

# A script for creating a macOS launcher icon

set -e

DIR=$(dirname $0)
REPO="$DIR/.."
INPUT="$REPO/assets/logo_mac.png"
OUTPUT="$REPO/assets/briar.iconset"

echo "Creating OUTPUT directory"
mkdir -p "$OUTPUT"

echo "Creating images"

sips -z 16 16     $INPUT --out "${OUTPUT}/icon_16x16.png"
sips -z 32 32     $INPUT --out "${OUTPUT}/icon_16x16@2x.png"
sips -z 32 32     $INPUT --out "${OUTPUT}/icon_32x32.png"
sips -z 64 64     $INPUT --out "${OUTPUT}/icon_32x32@2x.png"
sips -z 128 128   $INPUT --out "${OUTPUT}/icon_128x128.png"
sips -z 256 256   $INPUT --out "${OUTPUT}/icon_128x128@2x.png"
sips -z 256 256   $INPUT --out "${OUTPUT}/icon_256x256.png"
sips -z 512 512   $INPUT --out "${OUTPUT}/icon_256x256@2x.png"
sips -z 512 512   $INPUT --out "${OUTPUT}/icon_512x512.png"
sips -z 1024 1024 $INPUT --out "${OUTPUT}/icon_512x512@2.png"

echo "Creating icns file"
iconutil -c icns $OUTPUT

rm -R $OUTPUT
