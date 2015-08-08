#!/bin/sh
export PATH=$PATH:${axoloti_runtime}/platform_linux/bin
echo "Compiling patch... with ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.patch
