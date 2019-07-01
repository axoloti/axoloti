#!/bin/bash

# Downloads and builds all the required dependencies and toolchain executables
# Items already present are skipped to save your bandwidth.

#checking for required executables
command -v 7z >/dev/null 2>&1 || { echo >&2 "I require 7zip (7z) but it's not installed.
Please install 7zip prior to running this script again. Aborting.
To install via brew run:
brew update
brew install p7zip
" ; exit 1; }


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

CH_VERSION=18.2.2
if [ ! -d "${PLATFORM_ROOT}/../chibios_${CH_VERSION}" ];
then
    cd "${PLATFORM_ROOT}/src"
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
#    mv ${ARDIR} chibios
    cd ${ARDIR}/ext
    7z x ./fatfs-0.13_patched.7z
    cd ../../
    mv ${ARDIR} ../..

    echo "fixing ChibiOS community from Axoloti/ChibiOS-Contrib"
    cd ${PLATFORM_ROOT}/../ChibiOS_${CH_VERSION}
    rm -rf community
    git clone https://github.com/axoloti/ChibiOS-Contrib.git community
    cd community
    git checkout patch-2
else
    echo "chibios directory already present, skipping..."
fi


if [ ! -f "${PLATFORM_ROOT}/gcc-arm-none-eabi-8-2018-q4-major/bin/arm-none-eabi-gcc" ];
then
    cd "${PLATFORM_ROOT}"
    ARDIR=gcc-arm-none-eabi-8-2018
    ARCHIVE_BASE="gcc-arm-none-eabi-8-2018-q4-major"
    ARCHIVE=${ARCHIVE_BASE}-mac.tar.bz2
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://armkeil.blob.core.windows.net/developer/Files/downloads/gnu-rm/8-2018q4/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi
    tar xfj ${ARCHIVE}
    rm ${ARCHIVE}
else
    echo "gcc-arm-none-eabi-8-2018-q4-major/bin/arm-none-eabi-gcc already present, skipping..."
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

    ./configure --prefix="${PLATFORM_ROOT}" CFLAGS="-mmacosx-version-min=10.6"
    make
    make install
    make clean
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

    cd "$PLATFORM_ROOT/src/$ARDIR"
    ./configure --prefix="${PLATFORM_ROOT}" USB_LIBS="${PLATFORM_ROOT}/lib/libusb-1.0.0.dylib" USB_CFLAGS=-I${PLATFORM_ROOT}/include/libusb-1.0/ CFLAGS="-mmacosx-version-min=10.6"
    make clean
    make
    make install
    make clean
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
    ./configure --prefix="${PLATFORM_ROOT}" CFLAGS="-mmacosx-version-min=10.6"
    make 
    make install
    make clean
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
