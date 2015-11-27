#!/bin/bash

set -e

if [ ! -d "../chibios" ]; 
then
    ARDIR=ChibiOS_2.6.8
    ARCHIVE=${ARDIR}.zip
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://sourceforge.net/projects/chibios/files/ChibiOS_RT%20stable/Version%202.6.8/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi

    unzip -o ${ARCHIVE}
    rm ${ARCHIVE}
    mv ${ARDIR} chibios
    cd chibios/ext
    unzip -o ./fatfs-0.9-patched.zip
    cd ../../
    mv chibios ..
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
    unzip -o ${ARCHIVE}
    rm ${ARCHIVE}
fi

if [ ! -f "bin/make.exe" ];
then
    echo "downloading make"
    curl -L http://gnuwin32.sourceforge.net/downlinks/make-bin-zip.php > make-3.81-bin.zip
    unzip -o make-3.81-bin.zip 
    rm make-3.81-bin.zip
fi


if [ ! -f "bin/libiconv2.dll" ];
then
    echo "downloading make-dep"
    curl -L http://gnuwin32.sourceforge.net/downlinks/make-dep-zip.php > make-3.81-dep.zip
    unzip -o make-3.81-dep.zip
    rm make-3.81-dep.zip
fi

if [ ! -f "bin/rm.exe" ];
then
    echo "downloading rm"
    curl -L http://gnuwin32.sourceforge.net/downlinks/coreutils-bin-zip.php > coreutils-5.3.0-bin.zip
    unzip -o coreutils-5.3.0-bin.zip
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
        curl -L http://archive.apache.org/dist/ant/binaries/${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi    

    unzip ${ARCHIVE}
    rm ${ARCHIVE}
fi

if [ ! -f "zadig_2.1.2.exe" ];
then
    ARCHIVE=zadig_2.1.2.exe
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://zadig.akeo.ie/downloads/${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi        
fi


echo "DONE!"
