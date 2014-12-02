@ECHO OFF
CALL %~dp0\path.bat
cd %~dp0..\firmware
mkdir .dep
mkdir build
mkdir build\obj
mkdir build\lst
make all
rem cd flasher
rem make all
