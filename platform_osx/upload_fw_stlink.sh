#!/bin/sh
cd platform_osx/bin
./st-flash write ./firmware/build/axoloti.bin 0x08000000
