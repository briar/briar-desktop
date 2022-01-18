#!/bin/bash

# Install gedit as a random package that ensures `/usr/share/metainfo` is available
apt-get update
DEBIAN_FRONTEND=noninteractive apt-get install -y xauth xdg-utils libnotify4 libasound2 gedit

wget -O briar.deb "https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-ubuntu-18.04.deb?job=b_package_linux"
sudo dpkg -i briar.deb
