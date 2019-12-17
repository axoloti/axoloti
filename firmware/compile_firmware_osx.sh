#!/bin/sh
set -e

. ${axoloti_runtime}/platform_osx/path.sh

echo "Compiling firmware... ${axoloti_firmware}"
cd "${axoloti_firmware}"

mkdir -p build/obj
mkdir -p build/lst
if ! make $1; then
    exit 1
fi

echo "Compiling firmware mounter..."
cd mounter
mkdir -p mounter_build/obj
mkdir -p mounter_build/lst
make $1
cd ..
