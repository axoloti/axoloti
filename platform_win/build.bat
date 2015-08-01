@echo off

setlocal

cd %~sdp0

set MINGW=C:\MinGW
set MINGWGET=%MINGW%\bin\mingw-get.exe

if not exist %MINGWGET% (
  echo MinGW not installed
  echo download and run http://www.mingw.org/download/installer
  echo and then re-run this script
  echo launching a browser for you...
  start /max http://www.mingw.org/download/installer
  pause
  goto :end
)

%MINGWGET% install mingw32-gcc-g++ autoconf msys-bash libtool libz msys-make msys-wget msys-unzip msys-diffutils msys-patch

set PATH=%PATH%;%MINGW%\msys\1.0\bin;%MINGW%\bin
set HOME=.
%MINGW%\msys\1.0\bin\bash.exe build.sh

call build_gui.bat
call compile_firmware.bat

echo READY
echo Launch Axoloti by double clicking Axoloti\axoloti.bat
echo then flash the firmware

:end
endlocal
