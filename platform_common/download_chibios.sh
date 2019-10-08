source ../firmware/ch_version.mk

CHIBIOS_ARDIR=ChibiOS_${CH_VERSION}
CHIBIOS_ARCHIVE=${CHIBIOS_ARDIR}.7z
CHIBIOS_DOWNLOAD_URL=https://osdn.net/projects/chibios/downloads/70739/${CHIBIOS_ARCHIVE}

if [ ! -d "${PLATFORM_ROOT}/../chibios_${CH_VERSION}" ];
then
    cd "${PLATFORM_ROOT}/src"
    if [ ! -f ${CHIBIOS_ARCHIVE} ];
    then
        echo "downloading ${CHIBIOS_ARCHIVE}"
        curl -L ${CHIBIOS_DOWNLOAD_URL} > ${CHIBIOS_ARCHIVE}
    else
        echo "${CHIBIOS_ARCHIVE} already downloaded"
    fi
    7z x ${CHIBIOS_ARCHIVE}
    cd ${CHIBIOS_ARDIR}/ext
    7z x ./fatfs-0.13_patched.7z
    cd ../../
    mv ${CHIBIOS_ARDIR} ../..

    echo "fixing ChibiOS community from Axoloti/ChibiOS-Contrib"
    cd ${PLATFORM_ROOT}/../ChibiOS_${CH_VERSION}
    rm -rf community
    git clone https://github.com/axoloti/ChibiOS-Contrib.git community
    cd community
    git checkout patch-2
else
    echo "chibios directory already present, skipping..."
fi
