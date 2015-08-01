
set PATH=%axoloti_runtime%\bin

echo "setup build dir"
cd %axoloti_firmware%
if not exist ".dep\" mkdir .dep
if not exist "build\" mkdir build
if not exist "build\obj\" mkdir build\obj
if not exist "build\lst\" mkdir build\lst

echo "Compiling firmware..."
make -f Makefile.patch clean
make

echo "Compiling firmware flasher..."
cd flasher
if not exist ".dep\" mkdir .dep
if not exist "flasher_build\" mkdir flasher_build
if not exist "flasher_build\obj\" mkdir flasher_build\obj
if not exist "flasher_build\lst\" mkdir flasher_build\lst
make
