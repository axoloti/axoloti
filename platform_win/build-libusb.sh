#!/bin/bash

##########################################################################
# download and build libusb and dfu-util 
# for windows, using MinGW
# 
# If you don't have MinGW:
# * download and run the MinGW installer using default settings
# * run build.bat, this launches a MinGW shell
# * in the MinGW shell, launch this script with the command "./build.sh"
##########################################################################

mount c:/MinGW/ /mingw
mount c:/MinGW/ /usr/local

set -e

BUILD_DIR=$PWD
MINGW_VERSION=i586-mingw32msvc
PKG_CONFIG_PATH=$BUILD_DIR/lib/pkgconfig

##########################################################################
# libusb
##########################################################################

cd src

if [ ! -d "libusb-1.0.19" ]; 
then
    echo "##### Downloading, extracting an patching libusb ######"
    if [ ! -f "libusb-1.0.19.tar.bz2" ];
    then
        #wget --no-check-certificate --content-disposition http://github.com/libusb/libusb/archive/v1.0.19.tar.gz 
        wget -O libusb-1.0.19.tar.bz2 http://sourceforge.net/projects/libusb/files/libusb-1.0/libusb-1.0.19/libusb-1.0.19.tar.bz2/download
    fi
    tar jxvf libusb-1.0.19.tar.bz2
    cd libusb-1.0.19/
    patch -N -p1 < ../libusb.mingw32.patch
    patch -N -p1 < ../libusb.stdfu.patch
    cd ..
fi

echo "##### Compiling libusb ######"

cd libusb-1.0.19/
if [ ! -f "Makefile" ];
then
# CC='gcc -static-libgcc' to avoid libgcc_s_dw2-1.dll dependency
    ./configure --prefix=$PWD/../../ CC='gcc -static-libgcc'
fi
make CFLAGS="-DWINVER=0x0501"
make install
cd ..



##########################################################################
# dfu-util
#
# does not work, reports 
#   "dfuse_download: libusb_control_transfer returned -2"
##########################################################################
#
#if [ ! -d "dfu-util-0.8" ]; 
#then
#    echo "##### Downloading and extracting dfu-util ######"
#    if [ ! -f "dfu-util-0.8.tar.gz" ];
#    then
#        wget -nc http://dfu-util.sourceforge.net/releases/dfu-util-0.8.tar.gz
#    fi
#    tar xfv dfu-util-0.8.tar.gz
#fi
#
#echo "##### Compiling dfu-util ######"
#cd dfu-util-0.8
#if [ ! -f "Makefile" ];
#then
#    ./configure --host=$MINGW_VERSION --prefix=$BUILD_DIR
## CFLAGS=-I$PWD/../../include/libusb-1.0 USB_CFLAGS=-I$PWD/../../include/libusb-1.0 USB_LIBS="-L$PWD/../../lib -lusb-1.0" --prefix=$PWD/../..
#fi
## make LDFLAGS=-static
#make install
#cd ..
