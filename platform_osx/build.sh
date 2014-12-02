#!/bin/bash

PLATFORM_ROOT=$(pwd)

mkdir bin
mkdir lib
mkdir src

cd $PLATFORM_ROOT/src
curl -L http://sourceforge.net/projects/chibios/files/ChibiOS_RT%20stable/Version%202.6.6/ChibiOS_2.6.6.zip > ChibiOS_2.6.6.zip
unzip -o ChibiOS_2.6.6.zip
mv ChibiOS_2.6.6 chibios
cd chibios/ext
unzip -o ./fatfs-0.9-patched.zip
cd ../../
mv chibios ../..


curl -L https://launchpad.net/gcc-arm-embedded/4.8/4.8-2014-q3-update/+download/gcc-arm-none-eabi-4_8-2014q3-20140805-mac.tar.bz2 > gcc-arm-none-eabi-4_8-2014q3-20140805-mac.tar.bz2
curl -L http://sourceforge.net/projects/libusb/files/libusb-1.0/libusb-1.0.9/libusb-1.0.9.tar.bz2 > libusb-1.0.9.tar.bz2
curl -L http://dfu-util.sourceforge.net/releases/dfu-util-0.8.tar.gz > dfu-util-0.8.tar.gz
curl -L https://github.com/texane/stlink/archive/master.zip > stlink-master.zip
curl -L http://ftp.gnu.org/gnu/make/make-3.82.tar.gz > make-3.82.tar.gz
tar xfvj gcc-arm-none-eabi-4_8-2014q3-20140805-mac.tar.bz2
mv gcc-arm-none-eabi-4_8-2014q3 ..
tar xfvj libusb-1.0.9.tar.bz2
tar xfvz dfu-util-0.8.tar.gz
unzip -o stlink-master.zip
tar xfvz make-3.82.tar.gz


cd $PLATFORM_ROOT/src/libusb-1.0.9
./configure --prefix=$PLATFORM_ROOT/i386 CFLAGS="-arch i386 -mmacosx-version-min=10.5" LDFLAGS="-arch i386"
make 
make install
make clean
./configure --prefix=$PLATFORM_ROOT/x86_64 CFLAGS="-arch x86_64 -mmacosx-version-min=10.5" LDFLAGS="-arch x86_64"
make 
make install
make clean

cd $PLATFORM_ROOT/
lipo -create x86_64/lib/libusb-1.0.0.dylib i386/lib/libusb-1.0.0.dylib -output lib/libusb-1.0.0.dylib

cd $PLATFORM_ROOT/lib
install_name_tool -id libusb-1.0.0.dylib libusb-1.0.0.dylib

cd $PLATFORM_ROOT/src/dfu-util-0.8
./configure --prefix=$PLATFORM_ROOT/i386 USB_LIBS=$PLATFORM_ROOT/lib/libusb-1.0.0.dylib USB_CFLAGS=-I$PLATFORM_ROOT/i386/include/libusb-1.0/ CFLAGS="-arch i386 -mmacosx-version-min=10.5" LDFLAGS="-arch i386"
make 
make install
make clean

cd $PLATFORM_ROOT/src/dfu-util-0.8
make clean
./configure --prefix=$PLATFORM_ROOT/x86_64 USB_LIBS=$PLATFORM_ROOT/lib/libusb-1.0.0.dylib USB_CFLAGS=-I$PLATFORM_ROOT/x86_64/include/libusb-1.0/ CFLAGS="-arch x86_64 -mmacosx-version-min=10.5" LDFLAGS="-arch x86_64"
make 
make install
make clean

cd $PLATFORM_ROOT/
lipo -create x86_64/bin/dfu-util i386/bin/dfu-util -output bin/dfu-util


cd $PLATFORM_ROOT/src/make-3.82
./configure --prefix=$PLATFORM_ROOT/i386 CFLAGS="-arch i386 -mmacosx-version-min=10.5" LDFLAGS="-arch i386"
make 
make install
make clean


cd $PLATFORM_ROOT/src/make-3.82
./configure --prefix=$PLATFORM_ROOT/x86_64 CFLAGS="-arch x86_64 -mmacosx-version-min=10.5" LDFLAGS="-arch x86_64"
make 
make install
make clean

cd $PLATFORM_ROOT/
lipo -create x86_64/bin/make i386/bin/make -output bin/make


cd $PLATFORM_ROOT/src/stlink-master
./autogen.sh
./configure --prefix=$PLATFORM_ROOT/i386 USB_LIBS=$PLATFORM_ROOT/lib/libusb-1.0.0.dylib USB_CFLAGS=-I$PLATFORM_ROOT/i386/include/libusb-1.0/ CFLAGS="-arch i386 -mmacosx-version-min=10.5" LDFLAGS="-arch i386"
make
make install
make clean

cd $PLATFORM_ROOT/src/stlink-master
./autogen.sh
./configure --prefix=$PLATFORM_ROOT/x86_64 USB_LIBS=$PLATFORM_ROOT/lib/libusb-1.0.0.dylib USB_CFLAGS=-I$PLATFORM_ROOT/x86_64/include/libusb-1.0/ CFLAGS="-arch x86_64 -mmacosx-version-min=10.5" LDFLAGS="-arch x86_64"
make
make install
make clean

cd $PLATFORM_ROOT/
lipo -create x86_64/bin/st-util i386/bin/st-util -output bin/st-util


cp -v $PLATFORM_ROOT/lib/* $PLATFORM_ROOT/bin/

file bin/make
file bin/st-util
file bin/dfu-util
file bin/libusb-1.0.0.dylib

