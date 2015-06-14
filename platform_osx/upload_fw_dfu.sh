#!/bin/bash
platformdir="$(cd $(dirname $0); pwd -P)"
cd ${platformdir}/bin
./dfu-util --device 0483:df11 -i 0 -a 0 -D ../../firmware/build/axoloti.bin --dfuse-address=0x08000000:leave
