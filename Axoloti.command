#!/bin/bash
rootdir="$(cd $(dirname $0); pwd -P)"
export axoloti_release=${axoloti_release:="$rootdir"}
export axoloti_runtime=${axoloti_runtime:="$rootdir"}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$rootdir"}

cd $rootdir

which java >/dev/null || echo "Java not found in path, please install Java..." 

if [ -f $rootdir/dist/Axoloti.jar ]
then
    java -Xdock:name=Axoloti -Xdock:icon=logo/axoloti_icon_128x128.png -jar $rootdir/dist/Axoloti.jar
    #kill -9 $(ps -p $(ps -p $PPID -o ppid=) -o ppid=) 
else
    echo "Axoloti.jar does not exist."
fi
