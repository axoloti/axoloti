#!/bin/bash

# this removes unnecessary code and archives
# created by running build.sh,
# but leaves all the binary dependencies
# so the whole axoloti folder can be moved to a 
# different machine that does not have xcode etc installed.

set -e

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

cd "$PLATFORM_ROOT"

rm src/ChibiOS_2.*.zip
rm src/dfu-util-0.8.tar.gz
rm src/libusb-1.0.19.tar.bz2
rm src/make-3.82.tar.gz

rm -rv src/dfu-util-0.8
rm -rv src/libusb-1.0.19
rm -rv src/make-3.82

rm gcc-arm-none-eabi-4_8-2014q3-20140805-mac.tar.bz2

rm -rv i386/lib/
rm -rv i386/share/
rm -rv i386/bin/
rm -rv i386/include/
rm -rv x86_64/lib/
rm -rv x86_64/share/
rm -rv x86_64/bin/
rm -rv x86_64/include/
rm -rv i386
rm -rv x86_64
