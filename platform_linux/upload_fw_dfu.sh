#!/bin/bash
platformdir="$(dirname $(readlink -f $0))"

export axoloti_release=${axoloti_release:="$platformdir/.."}
export axoloti_runtime=${axoloti_runtime:="$platformdir/.."}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$platformdir"}

if [ -f "${platformdir}/bin/dfu-util" ];
then
    cd "${platformdir}/bin"
    ./dfu-util --device 0483:df11 -i 0 -a 0 -D "${axoloti_firmware}/build/axoloti.bin" --dfuse-address=0x08000000:leave
else
    echo "dfu-util not found, run ./install.sh in axoloti/platform_linux"
fi
