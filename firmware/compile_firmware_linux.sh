#!/bin/sh
export PATH=$PATH:${axoloti_runtime}/platform_linux/bin
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
