echo off
cd %~dp0
powershell -ExecutionPolicy Bypass -File get_dependencies.ps1

echo ------------------------------------------------------------
echo - only downloaded the sources
echo - unzipping and organizing the files is not handled here yet
echo - read get_dependencies.sh
echo ------------------------------------------------------------

