@ECHO OFF
CALL %~sdp0\path.bat

DIR %axoloti_release%\ChibiOS_19.1.3 > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: chibios directory missing
	EXIT /b 1
)

DIR %axoloti_release%\ChibiOS_19.1.3\ext\fatfs > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: fatfs not found in chibios
	EXIT /b 1
)

make -v > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: make not in path
	EXIT /b 1
)

arm-none-eabi-gcc -v > NUL 2> NUL
IF %ERRORLEVEL% NEQ 0 (
	ECHO error: arm-none-eabi-gcc not in path
	EXIT /b 1
)

arm-none-eabi-gcc --version 2>&1 | FINDSTR /i "7-2018-q2" > NUL
IF %ERRORLEVEL% NEQ 0 (
   ECHO error: GCC version not matching:
   arm-none-eabi-gcc --version
   EXIT /b 1
)

ECHO Environment OK

