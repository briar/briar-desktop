#!/bin/bash

# A script for building a deb package on Debian based distributions.
# Expects JDKs 11 and 17 to be installed at /usr/lib/jvm/java-11-openjdk-amd64/
# and /usr/lib/jvm/java-17-openjdk-amd64/ respectively.

set -e

DIR=$(dirname $0)
REPO="$DIR/.."

cd "$REPO"
./gradlew -Dorg.gradle.java.home=/usr/lib/jvm/java-11-openjdk-amd64/ kaptKotlin
./gradlew -Dorg.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64/ -x kaptKotlin packageDeb
