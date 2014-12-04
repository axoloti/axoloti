#!/bin/bash

# zips the essentials for binary distribution

rm coreutils-5.3.0-bin.zip
rm make-3.81-bin.zip
rm make-3.81-dep.zip
rm ChibiOS_2.6.6.zip
rm gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip
rm stlink-20130324-win.zip
cd ..
zip -r Axoloti_win32.zip Axoloti.bat CMSIS chibios dist doc firmware fritzing license.txt manifest.mf README.md objects patch patches platform_win
