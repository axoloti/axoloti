@echo off

setlocal

::- Get the Java Version
set KEY="HKLM\SOFTWARE\JavaSoft\Java Development Kit"
set VALUE=CurrentVersion
reg query %KEY% /v %VALUE% 2>nul || (
	set KEY="HKLM\SOFTWARE\Wow6432Node\JavaSoft\Java Development Kit"
	reg query %KEY% /v %VALUE% 2>nul 
	) || (echo JDK not xxx installed @ exit /b 1)

set JDK_VERSION=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JDK_VERSION=%%b
)

echo JDK VERSION: %JDK_VERSION%

::- Get the JavaHome
set KEY="%KEY:"=%\%JDK_VERSION%"
set VALUE=JavaHome
reg query %KEY% /v %VALUE% 2>nul || (echo JavaHome not installed @ exit /b 1)

set JDKHOME=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE% ') do (
    set JDKHOME=%%b
)

echo JavaHome: %JDKHOME%

rem set PATH=%JDKHOME%

set PATH=%JDKHOME%;%JDKHOME%\bin;"C:\Program Files (x86)\NetBeans 8.0\extide\ant\bin";"C:\Program Files\NetBeans 8.0\extide\ant\bin"
echo %PATH%
cd %~dp0..\

ant

endlocal