#!/bin/sh
cd platform_osx
export PATH=$PATH:$PWD/gcc-arm-none-eabi-4_8-2014q3/bin/:$PWD/bin
#echo $PATH
#pwd
cd ../firmware
make
cd flasher
make

