SPACE :=
SPACE +=

axoloti_runtime ?= ..
axoloti_home ?= ..
ALLINC :=
MODULE_DEPS :=
MODULE_FILEDEPS :=

include arm-none-eabi.mk
include path.mk
include ${axoloti_api}/api.mk

BDIR=${axoloti_home}/build

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

#(info MODULE_PATHS = ${MODULE_PATHS})

# substitute semicolons with spaces
# on windows, spaces in paths are removed (using "short filenames")
# on osx/linux, spaces in paths are not supposed to exist,
# Make really can't deal with it

MODULE_PATHS := $(subst ;,$(SPACE),$(MODULE_PATHS))

$(foreach _MODULE_SRC_DIR,$(MODULE_PATHS), \
  $(eval $(info processing module $(_MODULE_SRC_DIR))) \
  $(eval MODULE_SRC_DIR := $(_MODULE_SRC_DIR)) \
  $(eval MODULE := $(notdir ${_MODULE_SRC_DIR})) \
  $(eval MODULE_BUILD_DIR := ${BDIR}/${MODULE}) \
  $(eval MODULE_BUILD_ELF := ${MODULE_BUILD_DIR}/${MODULE}.elf) \
  $(eval include ${_MODULE_SRC_DIR}/include.mk) \
)
#$(info  MODULE_DEPS ${MODULE_DEPS})
#$(info  MODULE_FILEDEPS ${MODULE_FILEDEPS})
#$(info  MODULE_FILEDEPSFILE ${BDIR}/filedeps.txt)

# Paths
#$(info ALLINC = ${ALLINC})
IINCDIR = $(patsubst %,-I"%",$(ALLINCDIR))
IINC = $(patsubst %,--include "%",$(ALLINC))
LLIBDIR = $(patsubst %,-L"%",$(DLIBDIR) $(ULIBDIR))

#$(info IINC = ${IINC})

XPATCH ?= ${BDIR}/xpatch

all : .filedeps ${XPATCH}.elf  ${XPATCH}.dbg.lst  ${XPATCH}.read

.SECONDARY:

.filedeps:
	@echo "$(MODULE_FILEDEPS)" > "${axoloti_home}/build/filedeps.txt"

%.o: %.cpp ${MODULE_DEPS}
	$(info  compiling ${<})
	@"$(CXX)" $(CCFLAGS) $(DEFS) $(IINCDIR) -MD -MP $(IINC) -c "$<" -o "$@"

%.dbg.elf : %.o
	$(info linking $(@:.o=))
	@"$(LD)" $(LDFLAGS) "$<" ${ALLLIBS} -T"${LDSCRIPT}" -Wl,-Map="$(@:.elf=.map)",--cref -o "$@" -lm

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
