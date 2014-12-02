@ECHO OFF
CALL %~dp0\path.bat
cd %~dp0\..\patch
make
