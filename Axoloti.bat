@echo off

cd %~dp0

setlocal

::- Get the Java Version
set KEY="HKLM\SOFTWARE\JavaSoft\Java Runtime Environment"
set VALUE=CurrentVersion
reg query %KEY% /v %VALUE% 2>nul || (
	set KEY="HKLM\SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment"
	reg query %KEY% /v %VALUE% 2>nul 
	) || (echo Java Runtime Environment not installed @ exit /b 1)
set JRE_VERSION=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JRE_VERSION=%%b
)

echo JRE VERSION: %JRE_VERSION%

::- Get the JavaHome
set KEY="%KEY:"=%\%JRE_VERSION%"
set VALUE=JavaHome
reg query %KEY% /v %VALUE% 2>nul || (echo JavaHome not installed @ exit /b 1)

set JAVAHOME=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JAVAHOME=%%b
)

set AXOLOTI_JAR=dist/axoloti.jar

if not exist %AXOLOTI_JAR% (
   echo Axoloti GUI is not compiled
   echo compile it by running platform_win\build_gui.bat
   pause
   goto :end
)

if not defined JAVAHOME (
   echo Java not installed, please install Java...
   pause
   goto :end
)

echo JavaHome: %JAVAHOME%

set PATH=%JAVAHOME%\bin
java -jar dist/axoloti.jar %*

:end
endlocal


