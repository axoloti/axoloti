@echo off
setlocal
call find_jdk || exit /b 1
call find_ant || exit /b 1
cd %~dp0\..
%ANT% || exit /b 1

endlocal
