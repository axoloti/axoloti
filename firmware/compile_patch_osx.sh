#!/bin/sh
export PATH=${axoloti_runtime}/platform_osx/gcc-arm-none-eabi-7-2017-q4-major/bin:${axoloti_runtime}/platform_osx/bin:$PATH
echo "Compiling patch... with ${axoloti_firmware}"
#cd "${axoloti_firmware}"
make -f "${axoloti_firmware}/Makefile.patch" "$@"
