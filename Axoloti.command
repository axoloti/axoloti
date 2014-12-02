#!/bin/bash
rootdir="$(cd $(dirname $0); pwd -P)"
cd $rootdir
java -Xdock:name=Axoloti -Xdock:icon=logo/axoloti_icon_128x128.png -Djava.library.path=$rootdir/dist/lib -jar $rootdir/dist/Axoloti.jar

#kill -9 $(ps -p $(ps -p $PPID -o ppid=) -o ppid=) 