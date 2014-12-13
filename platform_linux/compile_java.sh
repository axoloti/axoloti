#!/bin/bash
platformdir="$(dirname $(readlink -f $0))"
cd $platformdir/../

ant -Dplatforms.JDK_1.6.home=/usr/lib/jvm/default-java
