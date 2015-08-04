call %~sdp0\path.bat
dfu-util --device 0483:df11 -i 0 -a 0 -D "%axoloti_firmware%/build/axoloti.bin" --dfuse-address=0x08000000:leave
