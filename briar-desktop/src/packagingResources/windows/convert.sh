#!/bin/bash

inkscape -C -e banner.png banner.svg
inkscape -C -e dialog.png dialog.svg

convert banner.png banner.bmp
convert dialog.png dialog.bmp
