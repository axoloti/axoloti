@ECHO OFF

call :setfirmware "%axoloti_firmware%"
call :sethome "%axoloti_home%"
call :setrelease "%axoloti_release%"

set PATH=%axoloti_runtime%\platform_win\bin

echo "setup build dir"
cd %axoloti_firmware%
if not exist ".dep\" mkdir .dep
if not exist "build\" mkdir build
if not exist "build\obj\" mkdir build\obj
if not exist "build\lst\" mkdir build\lst

echo "Compiling firmware..."
make -f Makefile.patch clean
make
IF %ERRORLEVEL% NEQ 0 (
	exit /b 1
)

echo "Compiling firmware flasher..."
cd flasher
if not exist ".dep\" mkdir .dep
if not exist "flasher_build\" mkdir flasher_build
if not exist "flasher_build\obj\" mkdir flasher_build\obj
if not exist "flasher_build\lst\" mkdir flasher_build\lst
make
IF %ERRORLEVEL% NEQ 0 (
	exit /b 1
)
cd ..

echo "Compiling firmware mounter..."
cd mounter
if not exist ".dep\" mkdir .dep
if not exist "mounter_build\" mkdir mounter_build
if not exist "mounter_build\obj\" mkdir mounter_build\obj
if not exist "mounter_build\lst\" mkdir mounter_build\lst
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
