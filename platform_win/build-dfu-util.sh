#!/bin/bash

##########################################################################
# download and build libusb and dfu-util 
# for windows, using MSYS2/MINGW
# 
# required MSYS packages are fetched by build.bat
##########################################################################

set -e

BUILD_DIR=$PWD
MINGW_VERSION=i686-mingw-msys
PKG_CONFIG_PATH=$BUILD_DIR/lib/pkgconfig

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
if [ ! -f "Makefile" ];
then
# CC='gcc -static-libgcc' to avoid libgcc_s_dw2-1.dll dependency
    ./configure --host=$MINGW_VERSION --prefix=$BUILD_DIR 
#CC='gcc -static-libgcc'
fi
make CFLAGS="-DWINVER=0x0501"
make install
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
cd dfu-util-0.8
if [ ! -f "Makefile" ];
then
    CC=gcc -static
    ./configure --host=$MINGW_VERSION --prefix=$BUILD_DIR --prefix=$BUILD_DIR && make && make install
fi
cd ..