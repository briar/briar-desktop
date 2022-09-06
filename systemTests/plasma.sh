#!/bin/sh

export $(xargs -0 -a "/proc/$(pgrep plasmashell -n -U $UID)/environ") 2>/dev/null
export LD_LIBRARY_PATH=/run/current-system/sw/lib
java -jar /tmp/test.jar
