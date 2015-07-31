#!/bin/sh
export PATH=$PATH:${axoloti_runtime}/platform_linux/bin
cd ${axoloti_firmware}
make -f Makefile.patch
