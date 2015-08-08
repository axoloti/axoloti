#!/bin/sh
export PATH=$PATH:${axoloti_runtime}/platform_osx/bin
echo "Compiling patch... with ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.patch
