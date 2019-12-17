include ${axoloti_api}/api.mk
include ${axoloti_env}/path.mk
include ${axoloti_env}/arm-none-eabi.mk

DEFS = \
	-DCORTEX_USE_FPU=TRUE \
	-DTHUMB \
	-DTHUMB_PRESENT \
	-DTHUMB_NO_INTERWORKING \
	-DCORTEX_USE_FPU=TRUE \
	-DARM_MATH_CM4 \
	-D__FPU_PRESENT=1U \
	-DSTM32F427xx

CCFLAGS = \
	-mfloat-abi=hard \
	-mcpu=cortex-m4 \
	-mfpu=fpv4-sp-d16 \
	-mthumb \
	-mword-relocations \
	-mlong-calls

LDFLAGS = \
	-Bsymbolic \
	-mlong-calls \
	-fno-common \
	-nostartfiles \
	-mcpu=cortex-m4 \
	-mfloat-abi=hard \
	-mfpu=fpv4-sp-d16 \
	-mthumb \
	-mno-thumb-interwork \
	-L${axoloti_api}

LDSCRIPT = ${axoloti_env}/elf.ld
