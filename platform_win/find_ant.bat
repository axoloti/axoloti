
set ANT=%~sdp0\apache-ant-1.9.4\bin\ant.bat

echo ANT: %ANT%

if not exist %ANT% (
   echo ANT not found, please run build.bat first
   exit /b 1
)
