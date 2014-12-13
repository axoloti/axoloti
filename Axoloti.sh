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
esac

case "$platform" in
	mac)
		java -Xdock:name=AxoStudio -Djava.library.path=$rootdir/dist/lib -jar $rootdir/dist/Axoloti.jar
	;;
	linux)
#		java -Djava.library.path=$rootdir/dist/lib -jar $rootdir/dist/Axoloti.jar
#		java -cp $rootdir/dist/lib/jssc.jar -cp $rootdir/dist/lib/simple-xml-2.7.1.jar  -jar $rootdir/dist/Axoloti.jar
		java -Djava.library.path=$rootdir/dist/lib/jssc.jar -Djava.library.path=$rootdir/dist/lib/simple-xml-2.7.1.jar -jar $rootdir/dist/Axoloti.jar
	;;
esac
