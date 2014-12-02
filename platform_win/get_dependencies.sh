#!/bin/bash

wget -nc http://sourceforge.net/projects/chibios/files/ChibiOS_RT%20stable/Version%202.6.6/ChibiOS_2.6.6.zip
unzip -o ChibiOS_2.6.6.zip
mv ChibiOS_2.6.6 chibios
cd chibios/ext
unzip -o ./fatfs-0.9-patched.zip
cd ../../
mv chibios ..

wget -nc --no-check-certificate https://launchpad.net/gcc-arm-embedded/4.8/4.8-2014-q3-update/+download/gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip
unzip -o gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip 

wget -nc http://gnuwin32.sourceforge.net/downlinks/make-bin-zip.php
unzip -o make-3.81-bin.zip 

wget -nc http://gnuwin32.sourceforge.net/downlinks/make-dep-zip.php
unzip -o make-3.81-dep.zip

wget -nc http://gnuwin32.sourceforge.net/downlinks/coreutils-bin-zip.php
unzip -o coreutils-5.3.0-bin.zip

wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8-binaries/win32-mingw32/dfu-util.exe
wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8-binaries/win32-mingw32/libusb-1.0.dll

echo "if you need a pre-compiled openOCD, get it from here: http://www.freddiechopin.info/en/download/category/4-openocd"
