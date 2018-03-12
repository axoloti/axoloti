#!/bin/sh
export PATH=${axoloti_runtime}/platform_linux/gcc-arm-none-eabi-7-2017-q4-major/bin:${axoloti_runtime}/platform_linux/bin:$PATH
echo "Compiling patch... with ${axoloti_firmware}"
cd "${axoloti_firmware}"
make -f Makefile.patch $1
