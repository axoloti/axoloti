#!/bin/bash

set -e

echo -e "\nAxoloti build script for Linux"
echo -e "Downloads and builds all the required dependencies and toolchain executables"
echo -e "and all users will be granted permission to access Axoloti USB devices"
echo -e "Items already present are skipped."
echo -e "Use at your own risk.\n"
# echo -e "Press RETURN to continue\nCTRL-C if you are unsure!\n"
# read

PLATFORM_ROOT="$(cd $(dirname $0); pwd -P)"

ARCH=$(uname -m | sed 's/x86_//;s/i[3-6]86/32/')
if [ -f /etc/lsb-release ]; then
    . /etc/lsb-release
    OS=$DISTRIB_ID
elif [ -f /etc/debian_version ]; then
    OS=Debian  # XXX or Ubuntu??
    if [ -n "`grep 8.6 /etc/debian_version`" ] && [ -z "`uname -m | grep x86_64`" ]; then
      OS=DebianJessie32bit
    fi

elif [ -f /etc/arch-release ]; then
    OS=Archlinux
elif [ -f /etc/gentoo-release ]; then
    OS=Gentoo
elif [ -f /etc/fedora-release ]; then
    OS=Fedora
else
    OS=$(uname -s)
fi

if [[ "$1" == "--noninteractive" ]]; then
    noninteractive=true
else
    noninteractive=false
fi

if [[ $noninteractive != "true" ]]; then
    echo -e "The script can install the required packages using your package manager,"
    echo -e "For this you'll require sudo rights and need to enter your password..."
    read -p "Install required packages? [N/y] " input
fi

if [[ $noninteractive == "true" || $input == "Y" || $input == "y" ]]; then
case $OS in
    Ubuntu|Debian|DebianJessie32bit)
        if [ $OS==DebianJessie32bit ]; then
            echo "apt-get install -y build-essential libtool libudev-dev automake autoconf ant curl p7zip-full unzip udev openjdk-8-jdk"
            sudo apt-get install -y build-essential libtool libudev-dev automake autoconf \
               ant curl p7zip-full unzip udev openjdk-8-jdk
        else
            echo "apt-get install -y libtool libudev-dev automake autoconf ant curl p7zip-full openjdk-8-jdk"
            sudo apt-get install -y libtool libudev-dev automake autoconf \
               ant curl p7zip-full openjdk-8-jdk
        fi
        ;;
    Archlinux|Arch|ManjaroLinux)
        echo "pacman -Syy"
        sudo pacman -Syy
        echo "pacman -S --noconfirm apache-ant libtool automake autoconf curl openjdk-8-jdk"
        sudo pacman -S --noconfirm apache-ant libtool automake autoconf curl openjdk-8-jdk
        ;;
    Gentoo)
        echo "detected Gentoo"
        echo "emerge --update jdk:1.8 ant"
        sudo emerge --update jdk:1.8 ant
        ;;
    Fedora)
        echo "detected Fedora"
        sudo dnf group install "Development Tools"
        sudo dnf -y install libusb dfu-util libtool libudev-devel automake autoconf \
        ant curl bzip2
        ;;
    *)
        echo "Cannot handle dist: $OS"
        exit
        ;;
esac
fi

cd "$PLATFORM_ROOT"

./add_udev_rules.sh

mkdir -p "${PLATFORM_ROOT}/bin"
mkdir -p "${PLATFORM_ROOT}/lib"
mkdir -p "${PLATFORM_ROOT}/src"

git submodule update --init --recursive

source ../platform_common/download_chibios.sh

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
    tar xfj ${ARCHIVE}

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
    tar xfz ${ARCHIVE}

    cd "${PLATFORM_ROOT}/src/${ARDIR}"
    ./configure --prefix="${PLATFORM_ROOT}" USB_LIBS="${PLATFORM_ROOT}/lib/libusb-1.0.a -ludev -pthread" USB_CFLAGS="-I${PLATFORM_ROOT}/include/libusb-1.0/"
    make
    make install
    make clean
    ldd "${PLATFORM_ROOT}/bin/dfu-util"
else
    echo "##### dfu-util already present, skipping... #####"
fi

echo "##### compiling firmware... #####"
cd "${PLATFORM_ROOT}"
./compile_firmware.sh

echo "##### building GUI... #####"
cd "${PLATFORM_ROOT}"/..
ant

echo "DONE"
