#!/bin/bash

##########################################################################
# download and build libusb and dfu-util 
# for windows, using MSYS2/MINGW
# 
# required MSYS packages are fetched by build.bat
##########################################################################

set -e

PATH=/mingw32/bin:$PATH

BUILD_DIR=$PWD
MINGW_VERSION=i686-mingw-msys
export PKG_CONFIG_PATH=$BUILD_DIR/lib/pkgconfig
export CC="i686-w64-mingw32-gcc"
export LD="i686-w64-mingw32-gcc"

##########################################################################
# libusb
##########################################################################

cd src

if [ ! -d "libusb-1.0.19" ]; 
then
    echo "##### Downloading, extracting an patching libusb ######"
    ARDIR=libusb-1.0.19
    ARCHIVE=${ARDIR}.tar.bz2
    if [ ! -f ${ARCHIVE} ];
    then
        echo "##### downloading ${ARCHIVE} #####"
        curl -L http://sourceforge.net/projects/libusb/files/libusb-1.0/$ARDIR/$ARCHIVE/download > $ARCHIVE
    else
        echo "##### ${ARCHIVE} already downloaded #####"
    fi
    tar xfvj ${ARCHIVE}
    cd ${ARDIR}
    patch -N -p1 < ../libusb.mingw32.patch
    patch -N -p1 < ../libusb.stdfu.patch
    cd ..
fi

echo "##### Compiling libusb ######"

cd libusb-1.0.19/
if [ ! -f "{BUILD_DIR}/lib/libusb-1.0.a" ];
then
    ./configure --host=$MINGW_VERSION --prefix=$BUILD_DIR && make CFLAGS="-DWINVER=0x0501" && make install
fi
cd ..



##########################################################################
# dfu-util
##########################################################################

if [ ! -d "dfu-util-0.8" ]; 
then
    echo "##### Downloading, extracting dfu-util ######"
    ARDIR=dfu-util-0.8
    ARCHIVE=${ARDIR}.tar.gz
    if [ ! -f ${ARCHIVE} ];
    then
        echo "##### downloading ${ARCHIVE} #####"
        curl -L http://dfu-util.sourceforge.net/releases/$ARCHIVE > $ARCHIVE
    else
        echo "##### ${ARCHIVE} already downloaded #####"
    fi
    tar xfvz ${ARCHIVE}
fi

echo "##### Compiling dfu-util ######"
if [ ! -f "bin/dfu-util.exe" ];
then
	cd dfu-util-0.8
    ./configure --host=$MINGW_VERSION --prefix=$BUILD_DIR && make LDFLAGS="-static" && make install
	cd ..
fi
