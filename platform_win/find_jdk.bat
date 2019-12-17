
set JAVA_HOME=

:getjdklocation
rem Resolve location of Java JDK environment

set KeyName=HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Development Kit\1.8
set Cmd=reg query "%KeyName%" /s
for /f "tokens=2*" %%i in ('%Cmd% ^| findstr "JavaHome"') do set JAVA_HOME=%%j

set KeyName=HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\JDK\1.8
set Cmd=reg query "%KeyName%" /s
for /f "tokens=2*" %%i in ('%Cmd% ^| findstr "JavaHome"') do set JAVA_HOME=%%j

set KeyName=HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\JavaSoft\Java Development Kit\1.8
set Cmd=reg query "%KeyName%" /s
for /f "tokens=2*" %%i in ('%Cmd% ^| findstr "JavaHome"') do set JAVA_HOME=%%j

if not defined JAVA_HOME (
   echo JDK8 not installed, please install JDK8 first
   echo suggested JDK8 distribution: https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html
   exit /b 1
)
