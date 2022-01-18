#!/bin/bash

apt-get update
DEBIAN_FRONTEND=noninteractive apt-get install -y xdg-utils libasound2

wget https://code.briarproject.org/briar/briar-desktop/-/jobs/14954/artifacts/raw/briar-desktop-ubuntu.deb
