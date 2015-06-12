@ECHO OFF
call %~sdp0\path.bat
call %~sdp0\test_env.bat

cd %FIRMWAREDIR%
if not exist ".dep\" mkdir .dep
if not exist "build\" mkdir build
if not exist "build\obj\" mkdir build\obj
if not exist "build\lst\" mkdir build\lst

make

cd %FIRMWAREDIR%\flasher
if not exist ".dep\" mkdir .dep
if not exist "build\" mkdir build
if not exist "build\obj\" mkdir build\obj
if not exist "build\lst\" mkdir build\lst
make
