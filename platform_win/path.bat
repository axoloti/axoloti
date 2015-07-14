set platformdir=%~sdp0

if not defined axoloti_release (
   set axoloti_release=%platformdir%..
)
if not defined axoloti_runtime (
   set axoloti_runtime=%platformdir%..
)
if not defined axoloti_firmware (
   set axoloti_firmware=%axoloti_release%\firmware
)
if not defined axoloti_home (
   set axoloti_home=%axoloti_release%\firmware
)
set PATH=%platformdir%\bin
