@ECHO OFF
CALL %~sdp0\path.bat
cd %axoloti_release%\patch
make
