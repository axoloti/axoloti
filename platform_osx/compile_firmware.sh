#!/bin/sh
set -e
platformdir="$(cd $(dirname $0); pwd -P)"

export axoloti_release=${axoloti_release:="$platformdir/.."}
export axoloti_runtime=${axoloti_runtime:="$platformdir/.."}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$platformdir/.."}

export PATH=$PATH:${platformdir}/bin
#echo $PATH

rm ${axoloti_home}/build/*.gch

echo "Compiling firmware..."
cd ${axoloti_firmware}
mkdir -p build/obj
mkdir -p build/lst
make

echo "Compiling firmware flasher..."
cd flasher
mkdir -p flasher_build/obj
mkdir -p flasher_build/lst
make
