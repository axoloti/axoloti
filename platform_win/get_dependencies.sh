#!/bin/bash

set -e

if [ ! -d "../chibios" ]; 
then
    wget http://sourceforge.net/projects/chibios/files/ChibiOS_RT%20stable/Version%202.6.6/ChibiOS_2.6.6.zip
    unzip -o ChibiOS_2.6.6.zip
    rm ChibiOS_2.6.6.zip
    mv ChibiOS_2.6.6 chibios
    cd chibios/ext
    unzip -o ./fatfs-0.9-patched.zip
    cd ../../
    mv chibios ..
fi

if [ ! -f "bin/arm-none-eabi-gcc.exe" ];
then
    wget -nc --no-check-certificate https://launchpad.net/gcc-arm-embedded/4.8/4.8-2014-q3-update/+download/gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip
    unzip -o gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip 
    rm gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip 
fi

if [ ! -f "bin/make.exe" ];
then
    wget -nc http://gnuwin32.sourceforge.net/downlinks/make-bin-zip.php
    unzip -o make-3.81-bin.zip 
    rm make-3.81-bin.zip
fi


if [ ! -f "bin/libiconv2.dll" ];
then
    wget -nc http://gnuwin32.sourceforge.net/downlinks/make-dep-zip.php
    unzip -o make-3.81-dep.zip
    rm make-3.81-dep.zip
fi

if [ ! -f "bin/rm.exe" ];
then
    wget -nc http://gnuwin32.sourceforge.net/downlinks/coreutils-bin-zip.php
    unzip -o coreutils-5.3.0-bin.zip
    rm coreutils-5.3.0-bin.zip
fi

cd bin
if [ ! -f "dfu-util.exe" ];
then
    wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8-binaries/win32-mingw32/dfu-util.exe
fi

if [ ! -f "dfu-prefix.exe" ];
then
    wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8-binaries/win32-mingw32/dfu-prefix.exe
fi

if [ ! -f "dfu-suffix.exe" ];
then
    wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8-binaries/win32-mingw32/dfu-suffix.exe
fi

cd ..

if [ ! -f "bin/st-flash.exe" ];
then
    echo to be completed: fetch st-flash.exe
#    wget -O st-flash-1.1.0.zip --no-check-certificate https://github.com/texane/stlink/archive/1.1.0.zip
fi

if [ ! -f "bin/libusb-1.0.dll" ];
then
    ./build-libusb.sh
fi

if [ ! -d "apache-ant-1.9.4" ];
then
    wget -nc http://archive.apache.org/dist/ant/binaries/apache-ant-1.9.4-bin.zip
    unzip apache-ant-1.9.4-bin.zip
    rm apache-ant-1.9.4-bin.zip
fi

echo "DONE!"
