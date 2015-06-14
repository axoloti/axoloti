#!/bin/sh
set -e
platformdir="$(dirname $(readlink -f $0))"
cd ${platformdir}/../firmware
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
cd ${platformdir}/../firmware/flasher
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
