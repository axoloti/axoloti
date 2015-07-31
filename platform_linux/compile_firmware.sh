#!/bin/sh
set -e
platformdir="$(dirname $(readlink -f $0))"

export axoloti_release=${axoloti_release:="$platformdir/.."}
export axoloti_runtime=${axoloti_runtime:="$platformdir/.."}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$platformdir/.."}

export PATH=$PATH:${platformdir}/bin

echo PLATFORMDIR=${platformdir}
echo PATH=$PATH


rm ${axoloti_home}/build/*.gch

cd ${axoloti_firmware}
echo "Compiling firmware..."
mkdir -p build/obj
mkdir -p build/lst
make

echo "Compiling firmware flasher..."
cd ${axoloti_firmware}/flasher
mkdir -p flasher_build/lst
mkdir -p flasher_build/obj
make
