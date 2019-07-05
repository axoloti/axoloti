@ECHO OFF
CALL %~sdp0\path.bat

DIR %axoloti_release%\ChibiOS_18.2.2 > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: chibios directory missing
	GOTO fail
)

DIR %axoloti_release%\ChibiOS_18.2.2\ext\fatfs > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: fatfs not found in chibios
	GOTO fail
)

make -v > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: make not in path
	GOTO fail
)

arm-none-eabi-gcc -v > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: arm-none-eabi-gcc not in path
	GOTO fail
)

arm-none-eabi-gcc --version 2>&1 | FINDSTR /i "7-2018-q2" > NUL
IF %ERRORLEVEL% NEQ 0 (
   ECHO error: GCC version not matching:
   arm-none-eabi-gcc --version
   GOTO fail
)

ECHO Environment OK
goto :EOF

:fail
ECHO Environment incomplete, please run platform_win\build.bat...
PAUSE
