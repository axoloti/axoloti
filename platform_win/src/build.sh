#!/bin/sh

# mount c:/MinGW/ /mingw
# mount c:/MinGW/ /usr/local

wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8.tar.gz
tar xfv dfu-util-0.8.tar.gz
cd libusb-1.0.19/
./autogen.sh
./configure --prefix=$PWD/../../
make
make install
cd ..

wget --no-check-certificate -nc http://github.com/libusb/libusb/archive/v1.0.19.tar.gz
tar xfv v1.0.19

cd dfu-util-0.8

./configure USB_CFLAGS=-I$PWD/../../include/libusb-1.0 USB_LIBS="-L$PWD/../../lib -lusb-1.0" --prefix=$PWD/../..
make LDFLAGS=-static
make install
