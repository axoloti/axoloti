#!/bin/sh

set -e

cd $(dirname $0)

# install udev rules

if [ ! -f /etc/udev/rules.d/49-axoloti.rules ]; 
then
   echo "##### copying 49-axoloti.rules rules to /etc/udev/rules.d/ #####"
   echo "For this you'll require sudo rights and need to enter your password..."
   sudo cp 49-axoloti.rules /etc/udev/rules.d/
# reload udev rules
   echo "##### reloading udev rules #####"
   sudo udevadm control --reload-rules
   echo "DONE"
else
   echo "##### udev rules already present, skipping #####"
fi
