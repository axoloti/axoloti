#!/bin/sh
. ${axoloti_runtime}/platform_linux/path.sh
echo "Compiling module... with ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.module $1
