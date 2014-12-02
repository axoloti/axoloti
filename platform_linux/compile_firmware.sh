#!/bin/sh
platformdir="$(dirname $(readlink -f $0))"
cd $platformdir/../
echo $PWD
cd firmware
make
cd flasher
make

