@ECHO OFF
CALL %~sdp0\path.bat

DIR %~sdp0\..\chibios > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: chibios directory missing
	GOTO fail
)

DIR %~sdp0\..\chibios\ext\fatfs > NUL 2> NUL
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

ECHO Environment OK
goto :EOF

:fail
ECHO Environment incomplete!
ECHO Run platform_win\get_dependencies.bat...
PAUSE