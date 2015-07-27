#!/bin/sh
set -e
platformdir="$(dirname $(readlink -f $0))"

export axoloti_release=${axoloti_release:="$platformdir/.."}
export axoloti_runtime=${axoloti_runtime:="$platformdir/.."}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$rootdir"}

export PATH=$PATH:${platformdir}/bin

echo PLATFORMDIR=${platformdir}
echo PATH=$PATH
ls ${platformdir}/bin

cd ${axoloti_firmware}
if [ ! -d "build" ]; 
then
    mkdir "build"
fi
if [ ! -d "build/obj" ]; 
then
    mkdir "build/obj"
fi
if [ ! -d "build/lst" ]; 
then
    mkdir "build/lst"
fi
echo "Compiling firmware..."
make
cd ${axoloti_firmware}/flasher
if [ ! -d "flasher_build" ]; 
then
    mkdir "flasher_build"
fi
if [ ! -d "flasher_build/obj" ]; 
then
    mkdir "flasher_build/obj"
fi
if [ ! -d "flasher_build/lst" ]; 
then
    mkdir "flasher_build/lst"
fi
echo "Compiling firmware flasher..."
make
