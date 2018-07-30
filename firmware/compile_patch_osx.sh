#!/bin/sh

. ${axoloti_runtime}/platform_osx/path.sh

echo "Compiling patch... with ${axoloti_firmware}"
#cd "${axoloti_firmware}"
make -f "${axoloti_firmware}/Makefile.patch" "$@"
