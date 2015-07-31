#!/bin/sh
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
