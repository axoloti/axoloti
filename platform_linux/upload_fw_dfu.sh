#!/bin/bash
platformdir="$(dirname $(readlink -f $0))"
cd $platformdir/../
dfu-util -v --device 0483:df11 -i 0 -a 0 -D firmware/build/axoloti.bin --dfuse-address=0x08000000:leave
