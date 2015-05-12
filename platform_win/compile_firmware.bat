@ECHO OFF
CALL %~dp0\path.bat
call %~dp0\test_env.bat

cd %FIRMWAREDIR%
mkdir .dep
mkdir build
mkdir build\obj
mkdir build\lst
make all
rem cd flasher
rem make all
