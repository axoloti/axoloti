#!/bin/bash
cd platform_osx/bin
./dfu-util --device 0483:df11 -i 0 -a 0 -D ../../firmware/build/axoloti.bin --dfuse-address=0x08000000