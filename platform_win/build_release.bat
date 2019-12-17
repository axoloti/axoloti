@echo off
setlocal
call find_jdk || exit /b 1
call find_ant || exit /b 1
cd %~dp0\..
set PATH=%PATH%;C:\Program Files (x86)\WiX Toolset v3.11\bin;C:\msys32\usr\bin

call %ANT% clean || exit /b 1
call %ANT% || exit /b 1
call %ANT% -Dbuild.bundle=true bundle || exit /b 1

:end
endlocal
