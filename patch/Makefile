CC=arm-none-eabi-gcc
CPP=arm-none-eabi-g++
LD=arm-none-eabi-gcc
CP=arm-none-eabi-objcopy
DMP=arm-none-eabi-objdump

axoloti_runtime ?= ..
axoloti_release ?= ..
axoloti_home ?= ..
axoloti_firmware ?= ../firmware
CHIBIOS = ${axoloti_release}/chibios
CMSIS = ${axoloti_release}/CMSIS
BDIR = ${axoloti_home}/build

CCFLAGS=  -fno-exceptions -fno-rtti -mcpu=cortex-m4 -O3 -fomit-frame-pointer -falign-functions=16 -mfloat-abi=hard -mfpu=fpv4-sp-d16 -fsingle-precision-constant -Wunused-parameter -DCORTEX_USE_FPU=TRUE -DTHUMB_PRESENT -mno-thumb-interwork -DTHUMB_NO_INTERWORKING -mthumb -DTHUMB -DARM_MATH_CM4 -D__FPU_PRESENT
CCFLAGS11= -nostdlib -fno-exceptions -fno-rtti -mcpu=cortex-m4 -O3 -fomit-frame-pointer -falign-functions=16 -mfloat-abi=hard -mfpu=fpv4-sp-d16 -fsingle-precision-constant -Wunused-parameter -DCORTEX_USE_FPU=TRUE -DTHUMB_PRESENT -mno-thumb-interwork -DTHUMB_NO_INTERWORKING -mthumb -DTHUMB -std=c++11 -DARM_MATH_CM4 -D__FPU_PRESENT
LDFLAGS= -nostartfiles -T${axoloti_firmware}/patch.ld -mcpu=cortex-m4 -mfloat-abi=hard -mfpu=fpv4-sp-d16 -mthumb  -mno-thumb-interwork


include $(CHIBIOS)/boards/ST_STM32F4_DISCOVERY/board.mk
include $(CHIBIOS)/os/hal/platforms/STM32F4xx/platform.mk
include $(CHIBIOS)/os/hal/hal.mk
include $(CHIBIOS)/os/ports/GCC/ARMCMx/STM32F4xx/port.mk
include $(CHIBIOS)/os/kernel/kernel.mk
include $(CHIBIOS)/os/various/fatfs_bindings/fatfs.mk

INCDIR = $(CMSIS)/Include \
		 $(PORTINC) $(KERNINC) $(TESTINC) \
         $(HALINC) $(PLATFORMINC) $(BOARDINC) $(FATFSINC) \
         ${axoloti_firmware}
         
# Paths
IINCDIR   = $(patsubst %,-I%,$(INCDIR) $(DINCDIR) $(UINCDIR))
LLIBDIR   = $(patsubst %,-L%,$(DLIBDIR) $(ULIBDIR))
           

all:
	@echo "RM"
	rm -f ${BDIR}/xpatch.o ${BDIR}/xpatch.elf ${BDIR}/xpatch.bin ${BDIR}/xpatch.d ${BDIR}/xpatch.map ${BDIR}/xpatch.lst
	@echo "APP"
	$(CPP) $(CCFLAGS11) $(IINCDIR) -MD -MP -c ${BDIR}/xpatch.cpp -o ${BDIR}/xpatch.o
	@echo LINK
	$(LD) $(LDFLAGS) ${BDIR}/xpatch.o -Wl,-Map=${BDIR}/xpatch.map,--cref,--just-symbols=${axoloti_firmware}/build/axoloti.elf -o ${BDIR}/xpatch.elf
	@echo BIN
	$(CP) -O binary ${BDIR}/xpatch.elf ${BDIR}/xpatch.bin
clean:
	rm -f ${BDIR}/xpatch.o ${BDIR}/xpatch.elf ${BDIR}/xpatch.bin ${BDIR}/xpatch.d ${BDIR}/xpatch.map ${BDIR}/xpatch.lst

