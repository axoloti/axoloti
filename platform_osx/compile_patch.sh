#!/bin/sh
set -e
platformdir="$(cd $(dirname $0); pwd -P)"
export axoloti_release=${axoloti_release:="$platformdir/.."}
export axoloti_runtime=${axoloti_runtime:="$platformdir/.."}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$rootdir"}

export PATH=$PATH:${platformdir}/gcc-arm-none-eabi-4_8-2014q3/bin/:${platformdir}/bin
cd ${axoloti_release}/patch
make
