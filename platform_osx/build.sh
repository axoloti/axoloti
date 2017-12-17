#!/bin/bash

# Downloads and builds all the required dependencies and toolchain executables
# Items already present are skipped to save your bandwidth.

set -e

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

cd "$PLATFORM_ROOT"

if [ ! -d "${PLATFORM_ROOT}/bin" ]; 
then
    mkdir "${PLATFORM_ROOT}/bin"
fi

if [ ! -d "${PLATFORM_ROOT}/lib" ]; 
then
    mkdir "${PLATFORM_ROOT}/lib"
fi

if [ ! -d "${PLATFORM_ROOT}/src" ]; 
then
    mkdir "${PLATFORM_ROOT}/src"
fi

if [ ! -d "${PLATFORM_ROOT}/../chibios" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    CH_VERSION=2.6.9
    ARDIR=ChibiOS_${CH_VERSION}
    ARCHIVE=${ARDIR}.zip
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://sourceforge.net/projects/chibios/files/ChibiOS%20GPL3/Version%20${CH_VERSION}/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi
    unzip -q -o ${ARCHIVE}
    mv ${ARDIR} chibios
    cd chibios/ext
    unzip -q -o ./fatfs-0.9-patched.zip
    cd ../../
    mv chibios ../..
else
    echo "chibios directory already present, skipping..."
fi

if [ ! -f "$PLATFORM_ROOT/bin/arm-none-eabi-gcc" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    ARCHIVE=gcc-arm-none-eabi-4_9-2015q2-20150609-mac.tar.bz2
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://launchpad.net/gcc-arm-embedded/4.9/4.9-2015-q2-update/+download/$ARCHIVE > $ARCHIVE
    else
        echo "${ARCHIVE} already downloaded"
    fi
    tar xfj ${ARCHIVE}
    cp -r gcc-arm-none-eabi-4_9-2015q2/* ..
    rm -r gcc-arm-none-eabi-4_9-2015q2
else
    echo "bin/arm-none-eabi-gcc already present, skipping..."
fi


if [ ! -f "$PLATFORM_ROOT/bin/libusb-1.0.0.dylib" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    ARDIR=libusb-1.0.19
    ARCHIVE=${ARDIR}.tar.bz2
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://sourceforge.net/projects/libusb/files/libusb-1.0/$ARDIR/$ARCHIVE/download > $ARCHIVE
    else
        echo "${ARCHIVE} already downloaded"
    fi
    tar xfj ${ARCHIVE}
    
    cd "${PLATFORM_ROOT}/src/libusb-1.0.19"

    patch -N -p1 < ../libusb.stdfu.patch

    ./configure --prefix="${PLATFORM_ROOT}/i386" CFLAGS="-arch i386 -mmacosx-version-min=10.6" LDFLAGS="-arch i386"
    make 
    make install
    make clean
    ./configure --prefix="${PLATFORM_ROOT}/x86_64" CFLAGS="-arch x86_64 -mmacosx-version-min=10.6" LDFLAGS="-arch x86_64"
    make 
    make install
    make clean

    cd $PLATFORM_ROOT/
    lipo -create x86_64/lib/libusb-1.0.0.dylib i386/lib/libusb-1.0.0.dylib -output lib/libusb-1.0.0.dylib

    cd $PLATFORM_ROOT/lib
    install_name_tool -id libusb-1.0.0.dylib libusb-1.0.0.dylib
else
    echo "libusb already present, skipping..."
fi

if [ ! -f "${PLATFORM_ROOT}/bin/dfu-util" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    ARDIR=dfu-util-0.8
    ARCHIVE=${ARDIR}.tar.gz
    if [ ! -f $ARCHIVE ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://dfu-util.sourceforge.net/releases/$ARCHIVE > $ARCHIVE
    else
        echo "$ARCHIVE already downloaded"
    fi
    tar xfz ${ARCHIVE}

    cd "${PLATFORM_ROOT}/src/${ARDIR}"
    ./configure --prefix="${PLATFORM_ROOT}/i386" USB_LIBS="${PLATFORM_ROOT}/lib/libusb-1.0.0.dylib" USB_CFLAGS=-I${PLATFORM_ROOT}/i386/include/libusb-1.0/ CFLAGS="-arch i386 -mmacosx-version-min=10.6" LDFLAGS="-arch i386"
    make 
    make install
    make clean

    cd "$PLATFORM_ROOT/src/$ARDIR"
    make clean
    ./configure --prefix="${PLATFORM_ROOT}/x86_64" USB_LIBS="${PLATFORM_ROOT}/lib/libusb-1.0.0.dylib" USB_CFLAGS=-I${PLATFORM_ROOT}/x86_64/include/libusb-1.0/ CFLAGS="-arch x86_64 -mmacosx-version-min=10.6" LDFLAGS="-arch x86_64"
    make 
    make install
    make clean

    cd "$PLATFORM_ROOT"
    lipo -create x86_64/bin/dfu-util i386/bin/dfu-util -output bin/dfu-util
else
    echo "dfu-util already present, skipping..."
fi

if [ ! -f "$PLATFORM_ROOT/bin/make" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    ARDIR=make-3.82
    ARCHIVE=${ARDIR}.tar.gz

    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://ftp.gnu.org/gnu/make/$ARCHIVE > $ARCHIVE
    else
        echo "${ARCHIVE} already downloaded"
    fi

    tar xfz $ARCHIVE

    cd "${PLATFORM_ROOT}/src/${ARDIR}"
    ./configure --prefix="${PLATFORM_ROOT}/i386" CFLAGS="-arch i386 -mmacosx-version-min=10.6" LDFLAGS="-arch i386"
    make 
    make install
    make clean

    cd "${PLATFORM_ROOT}/src/${ARDIR}"
    ./configure --prefix="${PLATFORM_ROOT}/x86_64" CFLAGS="-arch x86_64 -mmacosx-version-min=10.6" LDFLAGS="-arch x86_64"
    make 
    make install
    make clean

    cd "${PLATFORM_ROOT}"
    lipo -create x86_64/bin/make i386/bin/make -output bin/make
fi

cp -v "${PLATFORM_ROOT}/lib/"*.dylib "${PLATFORM_ROOT}/bin/"

file "${PLATFORM_ROOT}/bin/make"
file "${PLATFORM_ROOT}/bin/dfu-util"
file "${PLATFORM_ROOT}/bin/libusb-1.0.0.dylib"

echo "##### building firmware... #####"
cd "$PLATFORM_ROOT"
./compile_firmware.sh

echo "##### building GUI... #####"
cd "${PLATFORM_ROOT}"/..
ant

echo "DONE!"
