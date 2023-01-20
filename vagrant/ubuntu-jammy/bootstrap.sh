#!/bin/bash

apt-get update
DEBIAN_FRONTEND=noninteractive apt-get install -y xauth xdg-utils libnotify4 libasound2

wget -O briar.deb "https://code.briarproject.org/briar/briar-desktop/-/jobs/artifacts/main/raw/briar-desktop-ubuntu-22.04.deb?job=b_package_linux"
sudo dpkg -i briar.deb
