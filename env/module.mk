SPACE :=
SPACE +=
sq = $(subst $(SPACE),?,$1)
qs = $(subst ?,$(SPACE),$1)
qsb = $(subst ?,\ ,$1)
sb = $(subst $(SPACE),\ ,$1)

axoloti_api_= $(call sq,${axoloti_api})
$(info axoloti_api_ = ${axoloti_api_})

axoloti_env_=$(call sq,${axoloti_env})
#$(info axoloti_env_ = ${axoloti_env_})

include $(call sb,${axoloti_api}/api.mk)
include $(call sb,${axoloti_env}/path.mk)
include $(call sb,${axoloti_env}/arm-none-eabi.mk)

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
	-mthumb


LDFLAGS = \
	-Bsymbolic \
	-mlong-calls \
	-fno-common \
	-nostartfiles \
	-mcpu=cortex-m4 \
	-mfloat-abi=hard \
	-mfpu=fpv4-sp-d16 \
	-mthumb \
	-mno-thumb-interwork

LDSCRIPT = ${axoloti_env_}/elf.ld

