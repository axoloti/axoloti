#!/bin/sh
export PATH=$PATH:${axoloti_runtime}/platform_osx/bin
cd ${axoloti_firmware}
make -f Makefile.patch
