#!/bin/sh
export PATH=${axoloti_runtime}/platform_linux/bin:$PATH
echo "Compiling patch... with ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.patch
