#!/bin/bash
platformdir="$(dirname $(readlink -f $0))"

if [ -f "${platformdir}/bin/dfu-util" ];
then
    cd "${platformdir}"/../
    "${platformdir}"/bin/dfu-util --device 0483:df11 -i 0 -a 0 -D firmware/build/axoloti.bin --dfuse-address=0x08000000:leave
else
    echo "dfu-util not found, run ./install.sh in axoloti/platform_linux"
fi
