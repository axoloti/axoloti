@ECHO OFF
call %~sdp0\path.bat
rem echo path %PATH%
rem echo firmware %axoloti_firmware%
rem echo runtime %axoloti_runtime%
rem echo release %axoloti_release%
cd %axoloti_firmware%
call %axoloti_firmware%\compile_patch_win.bat
