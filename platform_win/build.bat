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
  exit /b 1
)

%PACMAN% pacman -S --needed git gcc autoconf libtool make wget unzip diffutils patch pkg-config mingw-w64-i686-gcc p7zip

set PATH=%PATH%;%MSYS32%\usr\bin
set HOME=.
%MSYS32%\usr\bin\bash.exe build.sh || exit /b 1

call compile_firmware.bat || exit /b 1
call build_gui.bat || exit /b 1

echo READY
echo Launch Axoloti by double clicking Axoloti\axoloti.bat
echo then flash the firmware

:end
endlocal
