#!/bin/sh
. ${axoloti_runtime}/platform_linux/path.sh

echo "Compiling patch... with ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.patch $1
