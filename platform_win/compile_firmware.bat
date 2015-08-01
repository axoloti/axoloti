@ECHO OFF
call %~sdp0\path.bat
call %~sdp0\test_env.bat

cd %axoloti_firmware%
call $axoloti_firmware%\compile_firmware_win.bat
