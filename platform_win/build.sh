#!/bin/bash

set -e

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

mkdir -p "${PLATFORM_ROOT}/src"

CH_VERSION=16.1.7
if [ ! -d "${PLATFORM_ROOT}/../chibios_${CH_VERSION}" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    ARDIR=ChibiOS_${CH_VERSION}
    ARCHIVE=${ARDIR}.zip
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://sourceforge.net/projects/chibios/files/ChibiOS_RT%20stable/Version%20${CH_VERSION}/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi
    unzip -q -o ${ARCHIVE}
#    mv ${ARDIR} chibios
    cd ${ARDIR}/ext
    7z x ./fatfs-0.10b-patched.7z
    cd ../../
    mv ${ARDIR} ../..
else
    echo "chibios directory already present, skipping..."
fi

if [ ! -f "bin/arm-none-eabi-gcc.exe" ];
then
    ARCHIVE=gcc-arm-none-eabi-4_9-2015q2-20150609-win32.zip
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://launchpad.net/gcc-arm-embedded/4.9/4.9-2015-q2-update/+download/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi    
    unzip -q -o ${ARCHIVE}
    rm ${ARCHIVE}
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
