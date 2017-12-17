#!/bin/bash

set -e

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

mkdir -p "${PLATFORM_ROOT}/src"
cd "${PLATFORM_ROOT}"

CH_VERSION=16.1.8
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
    7z x ./fatfs-0.10b-patched.7z
    cd ../../
    mv ${ARDIR} ../..

    echo "fixing ChibiOS community from ChibiOS-Contrib"
    cd ${PLATFORM_ROOT}/../chibios_${CH_VERSION}
    rm -rf community
    git clone https://github.com/axoloti/ChibiOS-Contrib.git community
    cd community 
    git checkout patch-1

    cd "${PLATFORM_ROOT}"
else
    echo "chibios directory already present, skipping..."
fi

if [ ! -f "${PLATFORM_ROOT}/gcc-arm-none-eabi-6-2017-q1-update/bin/arm-none-eabi-gcc.exe" ];
then
    ARDIR=gcc-arm-none-eabi-6-2017-q1-update
    ARCHIVE=${ARDIR}-win32.zip
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://armkeil.blob.core.windows.net/developer/Files/downloads/gnu-rm/6_1-2017q1/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi    
    unzip -q -o ${ARCHIVE} -d ${ARDIR}
    rm ${ARCHIVE}
else
    echo "gcc-arm-none-eabi-6-2017-q1-update/bin/arm-none-eabi-gcc already present, skipping..."
fi

if [ ! -f "bin/make.exe" ];
then
    echo "downloading make"
    curl -L http://gnuwin32.sourceforge.net/downlinks/make-bin-zip.php > make-3.81-bin.zip
    unzip -q -o make-3.81-bin.zip 
    rm make-3.81-bin.zip
fi


if [ ! -f "bin/libiconv2.dll" ];
then
    echo "downloading make-dep"
    curl -L http://gnuwin32.sourceforge.net/downlinks/make-dep-zip.php > make-3.81-dep.zip
    unzip -q -o make-3.81-dep.zip
    rm make-3.81-dep.zip
fi

if [ ! -f "bin/rm.exe" ];
then
    echo "downloading rm"
    curl -L http://gnuwin32.sourceforge.net/downlinks/coreutils-bin-zip.php > coreutils-5.3.0-bin.zip
    unzip -q -o coreutils-5.3.0-bin.zip
    rm coreutils-5.3.0-bin.zip
fi

if [ ! -f "bin/dfu-util.exe" ];
then
    ./build-dfu-util.sh
fi

if [ ! -d "apache-ant-1.9.4" ];
then
    ARCHIVE=apache-ant-1.9.4-bin.zip
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://archive.apache.org/dist/ant/binaries/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi    

    unzip -q ${ARCHIVE}
    rm ${ARCHIVE}
fi

if [ ! -f "zadig_2.1.2.exe" ];
then
    ARCHIVE=zadig_2.1.2.exe
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://zadig.akeo.ie/downloads/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi        
fi


echo "DONE!"
