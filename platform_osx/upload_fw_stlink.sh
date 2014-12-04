#!/bin/sh
cd platform_osx
cd bin
./st-flash write ../../firmware/build/axoloti.bin 0x08000000
