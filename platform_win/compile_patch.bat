@ECHO OFF
CALL %~sdp0\path.bat
cd %~sdp0\..\patch
make
