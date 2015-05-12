cd %~dp0
del coreutils-5.3.0-bin.zip
del make-3.81-bin.zip
del make-3.81-dep.zip
del ChibiOS_2.6.6.zip
del gcc-arm-none-eabi-4_8-2014q3-20140805-win32.zip
del stlink-20130324-win.zip
del src\libusb-1.0.19.tar.bz2

rmdir /S arm-none-eabi
rmdir /S bin
rmdir /S chibios
rmdir /S contrib
rmdir /S lib
rmdir /S man
rmdir /S manifest
rmdir /S share
rmdir /S include
rmdir /S stlink-20130324-win
rmdir /S src\libusb-1.0.19

