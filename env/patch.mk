SPACE :=
SPACE +=
sq = $(subst $(SPACE),?,$1)
qs = $(subst ?,$(SPACE),$1)
qsb = $(subst ?,\ ,$1)

axoloti_api_= $(call sq,${axoloti_api})
#$(info axoloti_api_ = ${axoloti_api_})

axoloti_env_=$(call sq,${axoloti_env})
#$(info axoloti_env_ = ${axoloti_env_})

axoloti_runtime ?= ..
axoloti_home ?= ..

include arm-none-eabi.mk
include path.mk
include $(subst $(SPACE),\ ,${axoloti_api}/api.mk)

BDIR=$(subst $(SPACE),\ ,${axoloti_home}/build)
# theoretically should be this...., 
# but CSRC expansion cannot cope with escape spaces [is this still an issue?]
# FIRMWARE=$(subst $(SPACE),\ ,${axoloti_firmware})

CCFLAGS = -ggdb3 \
	-std=c++11 \
	-mword-relocations \
	-mlong-calls \
	-mfloat-abi=hard \
	-mcpu=cortex-m4 \
	-mfpu=fpv4-sp-d16 \
	-mthumb \
	-nostdlib \
	-fno-common \
	-fno-exceptions \
	-fno-rtti \
	-fomit-frame-pointer \
	-fno-math-errno \
	-fno-threadsafe-statics \
	-fno-use-cxa-atexit \
	-fpermissive \
	-O3 \
	-Wno-unused-parameter

LDSCRIPT = ${axoloti_env}/elf.ld

LDFLAGS = -Wl,-Ur \
	-Bsymbolic \
	-mlong-calls \
	-fno-common \
	-nostartfiles \
	-mcpu=cortex-m4 \
	-mfloat-abi=hard \
	-mfpu=fpv4-sp-d16 \
	-mthumb \
	-mno-thumb-interwork \
	-Wl,--gc-sections \
	-Wl,--require-defined=getInstanceSize \
	-Wl,--require-defined=initInstance \
	-Wl,--wrap -Wl,memset \
	-Wl,--wrap -Wl,memcpy
# -Wl,--print-gc-sections

DEFS = \
	-DCORTEX_USE_FPU=TRUE \
	-DTHUMB \
	-DTHUMB_PRESENT \
	-DTHUMB_NO_INTERWORKING \
	-DCORTEX_USE_FPU=TRUE \
	-DARM_MATH_CM4 \
	-D__FPU_PRESENT=1U \
	-DSTM32F427xx

#$(info BDIR = ${BDIR})
#$(info ALLINCDIR = ${ALLINCDIR})
#$(info ALLINC = ${ALLINC})


default: all

#(info MODULEPATHS = ${MODULEPATHS})
$(foreach _MODULE_SRC_DIR,$(MODULEPATHS), \
  $(eval $(info processing module $(_MODULE_SRC_DIR)) \
  $(eval MODULE_SRC_DIR := $(call qs,$(_MODULE_SRC_DIR))) \
  $(eval MODULE = $(notdir ${_MODULE_SRC_DIR})) \
  $(eval include $(call qsb,${_MODULE_SRC_DIR}/include.mk))) \
)
$(info  MODULEDEPS ${MODULEDEPS})
$(info  MODULEFILEDEPS ${MODULEFILEDEPS})
$(info  MODULEFILEDEPSFILE ${BDIR}/filedeps.txt)

# Paths
#$(info ALLINC = ${ALLINC})
IINCDIR   = $(call qs,$(patsubst %,-I"%",$(ALLINCDIR)))
IINC   = $(call qs,$(patsubst %,--include "%",$(ALLINC)))
LLIBDIR   = $(call qs,$(patsubst %,-L"%",$(DLIBDIR) $(ULIBDIR)))

#$(info IINC = ${IINC})

XPATCH ?= ${BDIR}/xpatch

all : .filedeps ${XPATCH}.elf  ${XPATCH}.dbg.lst  ${XPATCH}.read

.SECONDARY:

.filedeps:
	@echo "$(MODULEFILEDEPS)" > "${axoloti_home}/build/filedeps.txt"

%.o: %.cpp ${MODULEDEPS}
	$(info  compiling ${<})
	@"$(CPPC)" $(CCFLAGS) $(DEFS) $(IINCDIR) -MD -MP $(IINC) -c "$<" -o "$@"

%.dbg.elf : %.o
	$(info linking $(@:.o=))
	@"$(LD)" $(LDFLAGS) "$<" ${ALLLIBS} -T"${LDSCRIPT}" -Wl,-Map="$(@:.elf=.map)",--cref -o "$@"

%.elf : %.dbg.elf
	@"$(STRP)" -g --strip-unneeded -o "$@" "$<"
	@"$(SZ)" -A "$@"

%.lst : %.elf
	@"$(OD)" -hpxdSsrt "$<" > "$@"

%.read : %.elf
	@"$(TRGT)readelf" -atSln "$<" > "$@"

%.clean :
	@rm -f "${XPATCH}.o"
	@rm -f "${XPATCH}.elf"
	@rm -f "${XPATCH}.read"
	@rm -f "${XPATCH}.lst"
	@rm -f "${XPATCH}.dbg.elf"
	@rm -f "${XPATCH}.dbg.read"
	@rm -f "${XPATCH}.dbg.lst"
	@rm -f "${XPATCH}.map"


clean: ${XPATCH}.clean
	$(info CLEAN)

