#!/bin/sh
platformdir="$(dirname $(readlink -f $0))"
cd $platformdir/../patch
make
