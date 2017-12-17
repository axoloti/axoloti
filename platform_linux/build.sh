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

case $OS in
    Ubuntu|Debian|DebianJessie32bit)
        echo "apt-get install -y libtool libudev-dev automake autoconf ant curl lib32z1 lib32ncurses5 lib32bz2-1.0"
      if [ $OS==DebianJessie32bit ]; then
            sudo apt-get install -y libtool libudev-dev automake autoconf \
               ant curl
      else
            sudo apt-get install -y libtool libudev-dev automake autoconf \
               ant curl lib32z1 lib32ncurses5
      fi

        # On more recent versions of Ubuntu
        # the libbz2 package is multi-arch
        install_lib_bz2() {
            sudo apt-get install -y lib32bz2-1.0
        }
        set +e
        if ! install_lib_bz2; then
            set -e
            sudo apt-get install -y libbz2-1.0:i386
        fi
        ;;
    Archlinux|Arch)
        echo "pacman -Syy"
        sudo pacman -Syy
        echo "pacman -S --noconfirm apache-ant libtool automake autoconf curl lib32-ncurses lib32-bzip2"
        sudo pacman -S --noconfirm apache-ant libtool automake autoconf curl \
             lib32-ncurses lib32-bzip2
        ;;
    Gentoo)
	echo "detected Gentoo"
	;;
    Fedora)
        echo "detected Fedora"
        sudo dnf group install "Development Tools"
        sudo dnf -y install libusb dfu-util libtool libudev-devel automake autoconf \
        ant curl ncurses-libs bzip2
        ;;
    *)
        echo "Cannot handle dist: $OS"
        exit
        ;;
esac

cd "$PLATFORM_ROOT"

./add_udev_rules.sh

mkdir -p "${PLATFORM_ROOT}/bin"
mkdir -p "${PLATFORM_ROOT}/lib"
mkdir -p "${PLATFORM_ROOT}/src"


if [ ! -d "${PLATFORM_ROOT}/../chibios" ];
then
    cd "${PLATFORM_ROOT}/src"
    CH_VERSION=2.6.9
    ARDIR=ChibiOS_${CH_VERSION}
    ARCHIVE=${ARDIR}.zip
    if [ ! -f ${ARCHIVE} ];
    then
        echo "##### downloading ${ARCHIVE} #####"
        curl -L https://sourceforge.net/projects/chibios/files/ChibiOS%20GPL3/Version%20${CH_VERSION}/${ARCHIVE} > ${ARCHIVE}
    else
        echo "##### ${ARCHIVE} already downloaded #####"
    fi
    unzip -q -o ${ARCHIVE}
    mv ${ARDIR} chibios
    cd chibios/ext
    unzip -q -o ./fatfs-0.9-patched.zip
    cd ../../
    mv chibios ../..
else
    echo "##### chibios directory already present, skipping... #####"
fi


if [ ! -f "$PLATFORM_ROOT/bin/arm-none-eabi-gcc" ];
then
    cd "${PLATFORM_ROOT}/src"
    ARCHIVE=gcc-arm-none-eabi-4_9-2015q2-20150609-linux.tar.bz2
    if [ ! -f ${ARCHIVE} ];
    then
        echo "downloading ${ARCHIVE}"
        curl -L https://launchpad.net/gcc-arm-embedded/4.9/4.9-2015-q2-update/+download/$ARCHIVE > $ARCHIVE
    else
        echo "${ARCHIVE} already downloaded"
    fi
    tar xfj ${ARCHIVE}
    cp -rv gcc-arm-none-eabi-4_9-2015q2/* ..
    rm -r gcc-arm-none-eabi-4_9-2015q2
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

case $OS in
    Ubuntu|Debian)
        echo "apt-get install openjdk-7-jdk"
        sudo apt-get install openjdk-7-jdk
        ;;
    Archlinux)
        echo "pacman -Syy jdk7-openjdk"
        sudo pacman -S --noconfirm jdk7-openjdk
        ;;
    Gentoo)
	echo "emerge --update jdk:1.7 ant"
	sudo emerge --update jdk:1.7 ant
	;;
esac


echo "##### compiling firmware... #####"
cd "${PLATFORM_ROOT}"
./compile_firmware.sh

echo "##### building GUI... #####"
cd "${PLATFORM_ROOT}"/..
ant

echo "DONE"
