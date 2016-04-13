#!/bin/sh
export PATH=${axoloti_runtime}/platform_osx/bin:$PATH

echo "Compiling firmware... ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.patch clean

mkdir -p build/obj
mkdir -p build/lst
if ! make $1; then
    exit 1
fi

echo "Compiling firmware flasher..."
cd flasher
mkdir -p flasher_build/obj
mkdir -p flasher_build/lst
make $1
cd ..

echo "Compiling firmware mounter..."
cd mounter
mkdir -p mounter_build/obj
mkdir -p mounter_build/lst
make $1
cd ..
