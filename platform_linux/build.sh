#!/bin/bash

set -e

echo -e "\n\nAxoloti Install script for Linux"
echo -e "This will install Axoloti"
echo -e "Use at your own risk\n"
echo -e "Some packages will be installed with apt-get,"
echo -e "and all users will be granted permission to access some USB devices"
echo -e "For this you'll require sudo rights and need to enter your password...\n"
# echo -e "Press RETURN to continue\nCTRL-C if you are unsure!\n"
# read


echo "apt-get install -y libtool libudev-dev automake autoconf ant curl lib32z1 lib32ncurses5 lib32bz2-1.0"
sudo apt-get install -y libtool libudev-dev automake autoconf ant curl lib32z1 lib32ncurses5 lib32bz2-1.0

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

cd "$PLATFORM_ROOT"

./add_udev_rules.sh

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
    ARDIR=ChibiOS_2.6.8
    ARCHIVE=${ARDIR}.zip
    if [ ! -f ${ARCHIVE} ]; 
    then
        echo "##### downloading ${ARCHIVE} #####"
        curl -L http://sourceforge.net/projects/chibios/files/ChibiOS_RT%20stable/Version%202.6.8/$ARCHIVE > $ARCHIVE
    else
        echo "##### ${ARCHIVE} already downloaded #####"
    fi
    unzip -o ${ARCHIVE}
    mv ${ARDIR} chibios
    cd chibios/ext
    unzip -o ./fatfs-0.9-patched.zip
    cd ../../
    mv chibios ../..
else
    echo "##### chibios directory already present, skipping... #####"
fi

if [ ! -f "$PLATFORM_ROOT/bin/arm-none-eabi-gcc" ];
then
    ARCHIVE=gcc-arm-none-eabi-4_9-2015q2-20150609-linux.tar.bz2
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://launchpad.net/gcc-arm-embedded/4.9/4.9-2015-q2-update/+download/$ARCHIVE > $ARCHIVE
    else
        echo "${ARCHIVE} already downloaded"
    fi
    tar xfvj ${ARCHIVE}
    cp -r gcc-arm-none-eabi-4_9-2015q2/* .
    rm -rv gcc-arm-none-eabi-4_9-2015q2
else
    echo "bin/arm-none-eabi-gcc already present, skipping..."
fi


if [ ! -f "$PLATFORM_ROOT/lib/libusb-1.0.a" ]; 
then
    cd "${PLATFORM_ROOT}/src"
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
    
    cd "${PLATFORM_ROOT}/src/libusb-1.0.19"

    patch -N -p1 < ../libusb.stdfu.patch

    ./configure --prefix="${PLATFORM_ROOT}"
    make 
    make install

else
    echo "##### libusb already present, skipping... #####"
fi

if [ ! -f "${PLATFORM_ROOT}/bin/dfu-util" ]; 
then
    cd "${PLATFORM_ROOT}/src"
    ARDIR=dfu-util-0.8
    ARCHIVE=${ARDIR}.tar.gz
    if [ ! -f $ARCHIVE ]; 
    then
        echo "##### downloading ${ARCHIVE} #####"
        curl -L http://dfu-util.sourceforge.net/releases/$ARCHIVE > $ARCHIVE
    else
        echo "##### ${ARCHIVE} already downloaded #####"
    fi
    tar xfvz ${ARCHIVE}

    cd "${PLATFORM_ROOT}/src/${ARDIR}"
    ./configure --prefix="${PLATFORM_ROOT}" USB_LIBS="${PLATFORM_ROOT}/lib/libusb-1.0.a -ludev -pthread" USB_CFLAGS="-I${PLATFORM_ROOT}/include/libusb-1.0/"
    make 
    make install
    make clean
    ldd "${PLATFORM_ROOT}/bin/dfu-util"
else
    echo "##### dfu-util already present, skipping... #####"
fi


sudo apt-get install openjdk-7-jdk

echo "##### building GUI... #####"
cd "${PLATFORM_ROOT}"/..
ant

echo "##### compiling firmware... #####"
cd "${PLATFORM_ROOT}"
./compile_firmware.sh

echo "DONE"

