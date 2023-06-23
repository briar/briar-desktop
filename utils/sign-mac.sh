#/bin/zsh

set -e

TEAM="ABCDEF1234"
APPLE_ID="<your apple id email>"
PASSWORD="<password>"

IDENTITY="Mobanisto"

SOURCE="../briar-desktop/build/pinpit/binaries/main-default/macos/arm64/distributableArchive/Briar-arm64-0.4.2.zip"
TARGET="Briar-release.zip"
APP="Briar.app"
ENTITLEMENTS_FILE="entitlements.plist"
LOG="Briar.log"

ENTITLEMENTS=$(realpath "$ENTITLEMENTS_FILE")

echo "Deleting target $TARGET"
rm -f "$TARGET"

echo "Deleting app dir $APP"
rm -rf "$APP"

echo "Unzipping source file $SOURCE"
unzip "$SOURCE"

echo "Signing tor binaries"
JARS=$(find Briar.app -name "tor-macos*" -or -name "obfs4proxy-macos*" -or -name "snowflake-macos*")
for jar in $JARS; do
    echo $jar
    unzip -d jardir $jar
    rm $jar
    cd jardir
    codesign -f -s "$IDENTITY" --options runtime --entitlements "$ENTITLEMENTS" $(find . -type f)
    zip -r ../$jar *
    cd ..
    rm -rf jardir
done

echo "Signing app $APP"
codesign -f -s "$IDENTITY" --options runtime --entitlements "$ENTITLEMENTS" $(find "$APP" -name "*.dylib" -or -name jspawnhelper)
codesign -f -s "$IDENTITY" --options runtime --entitlements "$ENTITLEMENTS" "$APP"
codesign -vvv --deep --strict "$APP"

echo "Zipping app to $TARGET"
ditto -c -k --keepParent "$APP" "$TARGET"

echo "Uploading to notary service"
ID=$(xcrun notarytool submit "$TARGET" --team-id "$TEAM" --apple-id "$APPLE_ID" --password "$PASSWORD" --no-progress | grep "id:" | awk '{ print $2 }')
echo "Received ID: $ID"

echo "Waiting for notarization to complete"
xcrun notarytool wait --team-id "$TEAM" --apple-id "$APPLE_ID" --password "$PASSWORD" "$ID"

echo "Fetching notarization log to $LOG"
xcrun notarytool log --team-id "$TEAM" --apple-id "$APPLE_ID" --password "$PASSWORD" "$ID" "$LOG"
