##############################################################################
# Build global options
# NOTE: Can be overridden externally.
#

# Compiler options here.
ifeq ($(USE_OPT),)
  USE_OPT = -O2 -ggdb -fomit-frame-pointer -falign-functions=16
endif

# C specific options here (added to USE_OPT).
ifeq ($(USE_COPT),)
  USE_COPT = -mlong-calls
endif

# C++ specific options here (added to USE_OPT).
ifeq ($(USE_CPPOPT),)
  USE_CPPOPT = -fno-rtti
endif

# Enable this if you want the linker to remove unused code and data.
ifeq ($(USE_LINK_GC),)
  USE_LINK_GC = yes
endif

# Linker extra options here.
ifeq ($(USE_LDOPT),)
  USE_LDOPT = --print-memory-usage \
              -Wl,-Ur \
              -Wl,-e_crt0_entry \
              -Wl,--require-defined=getInstanceSize
endif

# Enable this if you want link time optimizations (LTO)
ifeq ($(USE_LTO),)
  USE_LTO = no
endif

# Enable this if you want to see the full log while compiling.
ifeq ($(USE_VERBOSE_COMPILE),)
  USE_VERBOSE_COMPILE = no
endif

# If enabled, this option makes the build process faster by not compiling
# modules not used in the current configuration.
ifeq ($(USE_SMART_BUILD),)
  USE_SMART_BUILD = no
endif

#
# Build global options
##############################################################################

##############################################################################
# Architecture or project specific options
#

# Stack size to be allocated to the Cortex-M process stack. This stack is
# the stack used by the main() thread.
ifeq ($(USE_PROCESS_STACKSIZE),)
  USE_PROCESS_STACKSIZE = 0x400
endif

# Stack size to the allocated to the Cortex-M main/exceptions stack. This
# stack is used for processing interrupts and exceptions.
ifeq ($(USE_EXCEPTIONS_STACKSIZE),)
  USE_EXCEPTIONS_STACKSIZE = 0x400
endif

# Enables the use of FPU (no, softfp, hard).
ifeq ($(USE_FPU),)
  USE_FPU = hard
endif

# FPU-related options.
ifeq ($(USE_FPU_OPT),)
  USE_FPU_OPT = -mfloat-abi=$(USE_FPU) -mfpu=fpv4-sp-d16
endif

#
# Architecture or project specific options
##############################################################################

##############################################################################
# Project, target, sources and paths
#

# Define project name here
PROJECT = mounter.dbg

# Target settings.
MCU  = cortex-m4

BUILDDIR = mounter_build
DEPDIR = $(BUILDDIR)/.dep

# Imported source files and paths
include ../ch_version.mk

# Licensing files.
include $(CHIBIOS)/os/license/license.mk
# Startup files.
include $(CHIBIOS)/os/common/startup/ARMCMx/compilers/GCC/mk/startup_stm32f4xx.mk
include board.mk
include $(CHIBIOS)/os/common/ports/ARMCMx/compilers/GCC/mk/port_v7m.mk
include $(CHIBIOS_CONTRIB)/os/hal/hal.mk
include $(CHIBIOS_CONTRIB)/os/hal/ports/STM32/STM32F4xx/platform.mk
include $(CHIBIOS)/os/hal/osal/rt/osal.mk
# RTOS files (optional).
include $(CHIBIOS)/os/rt/rt.mk
include $(CHIBIOS)/os/hal/lib/streams/streams.mk

# Define linker script file here
LDSCRIPT = link-ch-in-patch.ld

# C sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CSRC = $(ALLCSRC) \
       ../axoloti_board.c \
       $(CHIBIOS_CONTRIB)/os/various/lib_scsi.c \
       usbcfg.c \
       main.c

# C++ sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CPPSRC = $(ALLCPPSRC)

# List ASM source files here.
ASMSRC = $(ALLASMSRC)

# List ASM with preprocessor source files here.
ASMXSRC = $(ALLXASMSRC)

# Inclusion directories.
INCDIR = $(CONFDIR) $(ALLINC) $(TESTINC) \
         $(CHIBIOS)/community/os/various/ \
         ../

# Define C warning options here.
CWARN = -Wall -Wextra -Wundef -Wstrict-prototypes

# Define C++ warning options here.
CPPWARN = -Wall -Wextra -Wundef

#
# Project, sources and paths
##############################################################################

##############################################################################
# Start of user section
#

# List all user C define here, like -D_DEBUG=1
UDEFS = -DSTM32F427xx -D__FPU_PRESENT=1U

# Define ASM defines here
UADEFS = \
  -DCRT0_INIT_DATA=0 \
  -DCRT0_INIT_BSS=0 \
  -DCRT0_INIT_RAM_AREAS=0 \
  -DCRT0_CALL_CONSTRUCTORS=0 \
  -DCRT0_CALL_DESTRUCTORS=0

# List all user directories here
UINCDIR = 

# List the user directory to look for the libraries here
ULIBDIR = .

# List all user libraries here
ULIBS = --specs=nosys.specs

VPATH = .

#
# End of user defines
##############################################################################

##############################################################################
# Common rules
#

RULESPATH = $(CHIBIOS)/os/common/startup/ARMCMx/compilers/GCC/mk
include $(RULESPATH)/arm-none-eabi.mk
include $(RULESPATH)/rules.mk

#
# Common rules
##############################################################################

##############################################################################
# Custom rules
#
%.elf: %.dbg.elf
	$(TRGT)strip -g --strip-unneeded -o $@ $<

%.lst: %.elf
	@$(OD) -hpxdSsrt $< > $@

%.read: %.elf
	@$(TRGT)readelf -atSln $< > $@

POST_MAKE_ALL_RULE_HOOK: \
	$(BUILDDIR)/mounter.elf \
	$(BUILDDIR)/mounter.lst \
	$(BUILDDIR)/mounter.read

#
# Custom rules
##############################################################################
