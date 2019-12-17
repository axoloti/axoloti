@ECHO OFF

call :setfirmware "%axoloti_firmware%"
call :sethome "%axoloti_home%"
call :setrelease "%axoloti_release%"

arm-none-eabi-gcc --version
IF %ERRORLEVEL% NEQ 0 (
	echo ERROR: arm-none-eabi-gcc not in path
	echo PATH=%PATH%
	exit /b 1
)
echo "setup build dir"
cd %axoloti_firmware%
if not exist ".dep\" mkdir .dep
if not exist "build\" mkdir build
if not exist "build\obj\" mkdir build\obj
if not exist "build\lst\" mkdir build\lst

echo "Compiling firmware..."
make
IF %ERRORLEVEL% NEQ 0 (
	exit /b 1
)

echo "Compiling firmware mounter..."
cd mounter
if not exist "mounter_build\" mkdir mounter_build
if not exist "mounter_build\obj\" mkdir mounter_build\obj
if not exist "mounter_build\lst\" mkdir mounter_build\lst
if not exist "mounter_build\.dep\" mkdir mounter_build\.dep
make
IF %ERRORLEVEL% NEQ 0 (
	exit /b 1
)
cd ..

goto :eof

rem --- path shortening

:setfirmware
set axoloti_firmware=%~s1
goto :eof

:sethome
set axoloti_home=%~s1
goto :eof

:setrelease
set axoloti_release=%~s1
goto :eof
