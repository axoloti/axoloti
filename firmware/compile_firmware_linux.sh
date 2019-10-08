#!/bin/sh
set -e

. ${axoloti_runtime}/platform_linux/path.sh

cd "${axoloti_firmware}"

echo "Compiling firmware... ${axoloti_firmware}"
mkdir -p build/obj
mkdir -p build/lst
if ! make $1 ; then
    exit 1
fi

echo "Compiling firmware mounter..."
cd mounter
mkdir -p mounter_build/lst
mkdir -p mounter_build/obj
make $1
cd ..
