#!/bin/bash
rootdir="$(cd $(dirname $0); pwd -P)"
cd $rootdir

which java >/dev/null || echo "Java not found in path, please install Java..." 

if [ -f $rootdir/dist/Axoloti.jar ]
then
    java -Xdock:name=Axoloti -Xdock:icon=logo/axoloti_icon_128x128.png -jar $rootdir/dist/Axoloti.jar
    #kill -9 $(ps -p $(ps -p $PPID -o ppid=) -o ppid=) 
else
    echo "Axoloti.jar does not exist."
fi
