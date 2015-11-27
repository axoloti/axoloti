@echo off

setlocal

cd %~sdp0

set MSYS32=c:\msys32
set PACMAN=%MSYS32%\usr\bin\pacman.exe

if not exist %PACMAN% (
  echo %PACMAN% not found,
  echo MSYS32 not installed
  echo download and run msys2-i686 from http://msys2.github.io/
  echo and then re-run this script
  echo launching a browser for you...
  start /max http://msys2.github.io/
  pause
  goto :end
)

%PACMAN% pacman -S --needed git gcc autoconf libtool make wget unzip diffutils patch pkg-config mingw-w64-i686-gcc

set PATH=%PATH%;%MSYS32%\usr\bin
set HOME=.
%MSYS32%\usr\bin\bash.exe build.sh

call build_gui.bat

call compile_firmware.bat

echo READY
echo Launch Axoloti by double clicking Axoloti\axoloti.bat
echo then flash the firmware

:end
endlocal
