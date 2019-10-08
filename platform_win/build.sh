#!/bin/bash

set -e

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

mkdir -p "${PLATFORM_ROOT}/src"
cd "${PLATFORM_ROOT}"

git submodule update

source ../platform_common/download_chibios.sh

if [ ! -f "${PLATFORM_ROOT}/gcc-arm-none-eabi-7-2018q2/bin/arm-none-eabi-gcc" ];
then
    cd "${PLATFORM_ROOT}"
    ARDIR=gcc-arm-none-eabi-7-2018q2
    ARCHIVE_BASE="gcc-arm-none-eabi-7-2018-q2-update"
    ARCHIVE=${ARCHIVE_BASE}-win32.zip
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
		curl -L https://armkeil.blob.core.windows.net/developer/Files/downloads/gnu-rm/7-2018q2/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi
    unzip -q -o ${ARCHIVE} -d ${ARDIR}
    rm ${ARCHIVE}
else
    echo "gcc-arm-none-eabi-7-2018q2 present, skipping..."
fi

if [ ! -f "bin/make.exe" ];
then
    echo "downloading make"
    curl -L http://gnuwin32.sourceforge.net/downlinks/make-bin-zip.php > make-3.81-bin.zip
    unzip -q -o make-3.81-bin.zip
    rm make-3.81-bin.zip
else
    echo "make already present, skipping...."
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
    echo "downloading gnuwin32 coreutils"
    curl -L http://gnuwin32.sourceforge.net/downlinks/coreutils-bin-zip.php > coreutils-5.3.0-bin.zip
    unzip -q -o coreutils-5.3.0-bin.zip
    rm coreutils-5.3.0-bin.zip
else
    echo "gnuwin32 coreutils already present, skipping...."
fi

if [ ! -f "bin/egrep.exe" ];
then
    echo "downloading grep"
    curl -L http://gnuwin32.sourceforge.net/downlinks/grep-bin-zip.php > grep-2.5.3-bin.zip
    unzip -q -o grep-2.5.3-bin.zip
    rm grep-2.5.3-bin.zip
else
    echo "gnuwin32 grep already present, skipping...."
fi

if [ ! -f "bin/regex2.dll" ];
then
    echo "downloading grep-dependencies"
    curl -L http://gnuwin32.sourceforge.net/downlinks/grep-dep-zip.php > grep-2.5.3-dep.zip
    unzip -q -o grep-2.5.3-dep.zip
    rm grep-2.5.3-dep.zip
else
    echo "gnuwin32 grep-dependencies already present, skipping...."
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

if [ ! -f "zadig_2.3.exe" ];
then
    ARCHIVE=zadig_2.3.exe
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://zadig.akeo.ie/downloads/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi
fi

if [ ! -f "bin/dfu-util-static.exe" ];
then
    ARCHIVE=dfu-util-0.9-win64.zip
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
        curl -L http://dfu-util.sourceforge.net/releases/${ARCHIVE} > ${ARCHIVE}
    else
        echo "${ARCHIVE} already downloaded"
    fi
	unzip -q -j -d bin dfu-util-0.9-win64.zip
fi

echo "done fetching sources..."
