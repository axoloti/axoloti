#!/bin/bash

platform='unknown'
unamestr=`uname`
case "$unamestr" in
	Linux)
		platform='linux'
		rootdir="$(dirname $(readlink -f $0))"
	;;
	Darwin)
		platform='mac'
		rootdir="$(cd $(dirname $0); pwd -P)"
	;;
        *)
                echo "unknown OS : $unamestr, aborting..."
                exit
        ;;
esac

which java >/dev/null || echo "java not found in path" 

if [ -f $rootdir/dist/Axoloti.jar ]
then
    case "$platform" in
        mac)
                java -Xdock:name=Axoloti -jar $rootdir/dist/Axoloti.jar
        ;;
        linux)
                java -jar $rootdir/dist/Axoloti.jar
        ;;
    esac
else
    echo "Axoloti.jar does not exist."
fi
