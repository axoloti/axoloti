@ECHO OFF
CALL %~sdp0\path.bat

DIR %axoloti_release%\ChibiOS_16.1.8 > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: chibios directory missing
	GOTO fail
)

DIR %axoloti_release%\ChibiOS_16.1.8\ext\fatfs > NUL 2> NUL
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

arm-none-eabi-gcc -v 2>&1 | FIND /i "6-2017-q1-update" > NUL
IF %ERRORLEVEL% NEQ 0 (
   ECHO GCC version not ok:
   arm-none-eabi-gcc -v
   GOTO fail
)

ECHO Environment OK
goto :EOF

:fail
ECHO Environment incomplete!
ECHO Run platform_win\build.bat...
PAUSE
