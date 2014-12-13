#!/bin/bash

# creates a binary distribution 
# (run after build.sh and ant)

cd platform_osx
./distclean.sh
cd ..
zip -r axoloti_osx.zip Axoloti.sh Axoloti.command CMSIS chibios dist doc firmware fritzing license.txt README.md manifest.mf objects patch patches platform_osx
