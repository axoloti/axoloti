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

export axoloti_release=${axoloti_release:="$rootdir"}
export axoloti_runtime=${axoloti_runtime:="$rootdir"}
export axoloti_firmware=${axoloti_firmware:="$axoloti_release/firmware"}
export axoloti_home=${axoloti_home:="$rootdir"}

which java >/dev/null || echo "java not found in path"

marlin_jvmargs='-Xbootclasspath/a:lib/marlin-0.9.1-Unsafe.jar -Dsun.java2d.renderer=org.marlin.pisces.MarlinRenderingEngine'

if [ -f "$rootdir/dist/Axoloti.jar" ]
then
    case "$platform" in
        mac)
                echo java $marlin_jvmargs -Xdock:name=Axoloti -jar "$rootdir/dist/Axoloti.jar" $*
                java $marlin_jvmargs -Xdock:name=Axoloti -jar "$rootdir/dist/Axoloti.jar" $* 2>&1 | tee "$axoloti_home/axoloti.log"
        ;;
        linux)
                echo java $marlin_jvmargs -jar $rootdir/dist/Axoloti.jar $*
                java $marlin_jvmargs -jar "$rootdir/dist/Axoloti.jar" $* 2>&1 | tee "$axoloti_home/axoloti.log"
        ;;
    esac
else
    echo "Axoloti.jar does not exist."
fi
