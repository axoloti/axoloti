source ../firmware/ch_version.mk

CHIBIOS_ARDIR=ChibiOS_${CH_VERSION}
CHIBIOS_ARCHIVE=${CHIBIOS_ARDIR}.7z
CHIBIOS_DOWNLOAD_URL=https://osdn.net/projects/chibios/downloads/70739/${CHIBIOS_ARCHIVE}

if [ ! -d "${PLATFORM_ROOT}/../${CHIBIOS_ARDIR}" ];
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

    echo "fetching ChibiOS-community"
    cd ${PLATFORM_ROOT}/../${CHIBIOS_ARDIR}
    rm -rf community
    git clone https://github.com/ChibiOS/ChibiOS-Contrib community
    cd community
    git checkout dc72ea603311123964271f910f051fb2027351ef
else
    echo "chibios directory already present, skipping..."
fi
