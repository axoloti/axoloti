#!/bin/bash

set -e

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

mkdir -p "${PLATFORM_ROOT}/src"
cd "${PLATFORM_ROOT}"

git submodule update --init --recursive

source ../platform_common/download_chibios.sh

if [ ! -f "bin/sh.exe" ];
then
    build_tools_fn=gnu-mcu-eclipse-windows-build-tools-2.12-20190422-1053-win32.zip
    echo "downloading ${build_tools_fn}"
    curl -L https://github.com/gnu-mcu-eclipse/windows-build-tools/releases/download/v2.12-20190422/${build_tools_fn} > ${build_tools_fn}
    unzip -q -o ${build_tools_fn}
    mv GNU\ MCU\ Eclipse/Build\ Tools/2.12-20190422-1053/bin/* bin/
    rm -r GNU\ MCU\ Eclipse/Build\ Tools/2.12-20190422-1053
    rm ${build_tools_fn}
else
    echo "make already present, skipping...."
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
