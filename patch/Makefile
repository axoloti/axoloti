CCFLAGS=  -fno-exceptions -fno-rtti -mcpu=cortex-m4 -O3 -fomit-frame-pointer -falign-functions=16 -mfloat-abi=hard -mfpu=fpv4-sp-d16 -fsingle-precision-constant -Wunused-parameter -DCORTEX_USE_FPU=TRUE -DTHUMB_PRESENT -mno-thumb-interwork -DTHUMB_NO_INTERWORKING -mthumb -DTHUMB -DARM_MATH_CM4 -D__FPU_PRESENT
CCFLAGS11= -nostdlib -fno-exceptions -fno-rtti -mcpu=cortex-m4 -O3 -fomit-frame-pointer -falign-functions=16 -mfloat-abi=hard -mfpu=fpv4-sp-d16 -fsingle-precision-constant -Wunused-parameter -DCORTEX_USE_FPU=TRUE -DTHUMB_PRESENT -mno-thumb-interwork -DTHUMB_NO_INTERWORKING -mthumb -DTHUMB -std=c++11 -DARM_MATH_CM4 -D__FPU_PRESENT
LDFLAGS= -nostartfiles -Tramlink.ld -mcpu=cortex-m4 -mfloat-abi=hard -mfpu=fpv4-sp-d16 -mthumb  -mno-thumb-interwork

#-mcpu=cortex-m4   
#-mno-thumb-interwork 
#-mthumb
#-Wl,-Map=build/ch.map,--cref,--no-warn-mismatch,--gc-sections 
 
CC=arm-none-eabi-gcc
CPP=arm-none-eabi-g++
LD=arm-none-eabi-gcc
CP=arm-none-eabi-objcopy
DMP=arm-none-eabi-objdump

CHIBIOS = ../chibios
CMSIS = ../CMSIS

include $(CHIBIOS)/boards/ST_STM32F4_DISCOVERY/board.mk
include $(CHIBIOS)/os/hal/platforms/STM32F4xx/platform.mk
include $(CHIBIOS)/os/hal/hal.mk
include $(CHIBIOS)/os/ports/GCC/ARMCMx/STM32F4xx/port.mk
include $(CHIBIOS)/os/kernel/kernel.mk
include $(CHIBIOS)/os/various/fatfs_bindings/fatfs.mk

INCDIR = $(CMSIS)/Include \
		 $(PORTINC) $(KERNINC) $(TESTINC) \
         $(HALINC) $(PLATFORMINC) $(BOARDINC) $(FATFSINC) \
         ../firmware
         
# Paths
IINCDIR   = $(patsubst %,-I%,$(INCDIR) $(DINCDIR) $(UINCDIR))
LLIBDIR   = $(patsubst %,-L%,$(DLIBDIR) $(ULIBDIR))
           

all:
	@echo "RM"
	rm -f xpatch.o xpatch.elf xpatch.bin xpatch.d xpatch.map xpatch.lst
	@echo "APP"
	$(CPP) $(CCFLAGS11) $(IINCDIR) -MD -MP -c xpatch.cpp -o xpatch.o
	@echo LINK
	$(LD) $(LDFLAGS) xpatch.o -Wl,-Map=xpatch.map,--cref,--just-symbols=../firmware/build/axoloti.elf -o xpatch.elf
	@echo BIN
#	$(CP) -O binary -j .text  -j .init_array -j .rodata -j .rodata\* xpatch.elf xpatch.bin
#   -j .text.startup -j .text.memcpy  
	$(CP) -O binary xpatch.elf xpatch.bin
#	@echo SDCARD
#	CP xpatch.bin T:\xpatch.bin
clean:
	rm -f xpatch.o xpatch.elf xpatch.bin xpatch.d xpatch.map xpatch.lst

