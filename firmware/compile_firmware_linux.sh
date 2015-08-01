#!/bin/sh
export PATH=$PATH:${axoloti_runtime}/platform_linux/bin

cd ${axoloti_firmware}
make -f Makefile.patch clean

echo "Compiling firmware..."
mkdir -p build/obj
mkdir -p build/lst
make

echo "Compiling firmware flasher..."
cd flasher
mkdir -p flasher_build/lst
mkdir -p flasher_build/obj
make
