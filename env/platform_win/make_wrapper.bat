:: Replace relevant paths in environment variables with "short" filenames
:: this avoids presence of space characters in paths that make can't handle...

@Echo off
SETLOCAL EnableDelayedExpansion
call :shortfilename  "%axoloti_api%"
set axoloti_api=%r%
call :shortfilename  "%axoloti_home%"
set axoloti_home=%r%
call :shortfilename  "%axoloti_release%"
set axoloti_release=%r%
call :shortfilename  "%axoloti_env%"
set axoloti_env=%r%

:: replace semicolon-separated list MODULE_PATHS with 
::   space-separated list of "short" paths...
::   except the last folder in the path, which is the module name...

::echo MODULE_PATHS=%MODULE_PATHS%
IF DEFINED MODULE_PATHS (
    set MODULE_PATHSX=
    FOR %%G IN ("%MODULE_PATHS:;=";"%") DO (
        set MODULE_PATHSX=!MODULE_PATHSX!;%%~dG%%~spG%%~nG
    )
    set MODULE_PATHSX=!MODULE_PATHSX:~1!
    call :setmodulepaths "!MODULE_PATHSX!"
)
echo MODULE_PATHS=!MODULE_PATHS!

make %*
goto :eof

:setmodulepaths
:: echo setmodulepaths %~1
set MODULE_PATHS=%~1
goto :eof

:shortfilename
set r=%~s1
goto :eof
