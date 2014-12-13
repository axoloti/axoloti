openocd -s "openocd/scripts/" -f "openocd/scripts/board/stm32f4discovery.cfg" -c "program firmware/build/axoloti.elf reset"
