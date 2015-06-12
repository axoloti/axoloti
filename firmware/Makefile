#############################################################################
# Build global options
# NOTE: Can be overridden externally.
#

# Compiler options here.
ifeq ($(USE_OPT),)
  USE_OPT = -O2 -ggdb -fomit-frame-pointer -falign-functions=16
endif

# C specific options here (added to USE_OPT).
ifeq ($(USE_COPT),)
  USE_COPT = 
endif
 
# C++ specific options here (added to USE_OPT).
ifeq ($(USE_CPPOPT),)
  USE_CPPOPT = -fno-rtti
endif

# Enable this if you want the linker to remove unused code and data
ifeq ($(USE_LINK_GC),)
  USE_LINK_GC = no
endif

# If enabled, this option allows to compile the application in THUMB mode.
ifeq ($(USE_THUMB),)
  USE_THUMB = yes
endif

# Enable this if you want to see the full log while compiling.
ifeq ($(USE_VERBOSE_COMPILE),)
  USE_VERBOSE_COMPILE = yes
endif

#
# Build global options
##############################################################################

##############################################################################
# Architecture or project specific options
#

# Enables the use of FPU on Cortex-M4 (no, softfp, hard).
ifeq ($(USE_FPU),)
  USE_FPU = hard
endif

#
# Architecture or project specific options
##############################################################################

##############################################################################
# Project, sources and paths
#

# Define project name here
PROJECT = axoloti

# Imported source files and paths
CHIBIOS = ../chibios

include board.mk
include $(CHIBIOS)/os/hal/platforms/STM32F4xx/platform.mk
include $(CHIBIOS)/os/hal/hal.mk
include $(CHIBIOS)/os/ports/GCC/ARMCMx/STM32F4xx/port.mk
include $(CHIBIOS)/os/kernel/kernel.mk
include $(CHIBIOS)/test/test.mk
	
# not using fatfs.mk, since ccsbcs.c should not be included in nonLFN fatfs config
# include $(CHIBIOS)/os/various/fatfs_bindings/fatfs.mk

#include $(CHIBIOS)/test/test.mk
FATFSSRC = ${CHIBIOS}/os/various/fatfs_bindings/fatfs_diskio.c \
           ${CHIBIOS}/os/various/fatfs_bindings/fatfs_syscall.c \
           ${CHIBIOS}/ext/fatfs/src/ff.c 

FATFSINC = ${CHIBIOS}/ext/fatfs/src

CMSIS = ../CMSIS/


# Define linker script file here
LDSCRIPT= STM32F407xG.ld


# C sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CSRC = $(PORTSRC) \
       $(KERNSRC) \
       $(HALSRC) \
       $(PLATFORMSRC) \
       $(BOARDSRC) \
       $(FATFSSRC) \
       $(CHIBIOS)/os/various/shell.c \
       $(CHIBIOS)/os/various/chprintf.c \
       $(CHIBIOS)/os/various/memstreams.c \
       axoloti_control.c \
       codec.c \
       axoloti.c \
       axoloti_filters.c \
       axoloti_math.c \
       axoloti_oscs.c \
       axoloti_board.c \
       midi.c \
       ui.c \
       glcdfont.c \
       usbcfg.c \
       sdcard.c \
       pconnection.c \
       parameters.c \
       main.c \
       patch.c \
       flash.c \
       virtual_control.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_dct4_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_bitreversal.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_mult_q31.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_shift_q31.c \
       ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mult_cmplx_q31.c \
       ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mag_q31.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_cos_q31.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_sin_q31.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_sqrt_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_fast_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_32x64_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_32x64_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_fast_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_interpolate_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_interpolate_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_decimate_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_decimate_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_decimate_fast_q31.c\
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_lattice_init_q31.c\
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_fir_lattice_q31.c\
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_iir_lattice_init_q31.c\
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_iir_lattice_q31.c\
       ${CMSIS}/DSP_Lib/Source/CommonTables/arm_common_tables.c \
       STM32_USB_Host_Library/Core/Src/usbh_core.c \
       STM32_USB_Host_Library/Core/Src/usbh_ctlreq.c \
       STM32_USB_Host_Library/Core/Src/usbh_ioreq.c \
       STM32_USB_Host_Library/Core/Src/usbh_pipes.c \
       STM32_USB_Host_Library/Class/HID/Src/usbh_hid.c \
       STM32_USB_Host_Library/Class/HID/Src/usbh_hid_keybd.c \
       STM32_USB_Host_Library/Class/HID/Src/usbh_hid_mouse.c \
       STM32_USB_Host_Library/Class/HID/Src/usbh_hid_parser.c \
       STM32F4xx_HAL_Driver/Src/stm32f4xx_ll_usb.c \
       STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_gpio.c \
       STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_cortex.c \
       STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_hcd.c \
       usbh_midi_core.c \
       usbh_conf.c \
       serial_midi.c \
       crc32.c \
       exceptions.c \
       watchdog.c 

# C++ sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CPPSRC =

# C sources to be compiled in ARM mode regardless of the global setting.
# NOTE: Mixing ARM and THUMB mode enables the -mthumb-interwork compiler
#       option that results in lower performance and larger code size.
ACSRC =

# C++ sources to be compiled in ARM mode regardless of the global setting.
# NOTE: Mixing ARM and THUMB mode enables the -mthumb-interwork compiler
#       option that results in lower performance and larger code size.
ACPPSRC =

# C sources to be compiled in THUMB mode regardless of the global setting.
# NOTE: Mixing ARM and THUMB mode enables the -mthumb-interwork compiler
#       option that results in lower performance and larger code size.
TCSRC =

# C sources to be compiled in THUMB mode regardless of the global setting.
# NOTE: Mixing ARM and THUMB mode enables the -mthumb-interwork compiler
#       option that results in lower performance and larger code size.
TCPPSRC =

# List ASM source files here
ASMSRC = $(PORTASM)

INCDIR = ../CMSIS/Include \
		 $(PORTINC) $(KERNINC) $(TESTINC) \
         $(HALINC) $(PLATFORMINC) $(BOARDINC) \
         $(FATFSINC) \
         $(CHIBIOS)/os/various \
         STM32_USB_Host_Library/Core/Inc \
         STM32_USB_Host_Library/Class/HID/Inc \
         STM32F4xx_HAL_Driver/Inc

#
# Project, sources and paths
##############################################################################

##############################################################################
# Compiler settings
#

MCU  = cortex-m4

#TRGT = arm-elf-
TRGT = arm-none-eabi-
CC   = $(TRGT)gcc
CPPC = $(TRGT)g++
# Enable loading with g++ only if you need C++ runtime support.
# NOTE: You can use C++ even without C++ support if you are careful. C++
#       runtime support makes code size explode.
LD   = $(TRGT)gcc 
#LD   = $(TRGT)g++ 
CP   = $(TRGT)objcopy
AS   = $(TRGT)gcc -x assembler-with-cpp
OD   = $(TRGT)objdump
SZ   = $(TRGT)size
HEX  = $(CP) -O ihex
BIN  = $(CP) -O binary

# ARM-specific options here
AOPT =

# THUMB-specific options here
TOPT = -mthumb -DTHUMB

# Define C warning options here
CWARN = -Wall -Wextra -Wstrict-prototypes

# Define C++ warning options here
CPPWARN = -Wall -Wextra

#
# Compiler settings
##############################################################################

##############################################################################
# Start of default section
#

# List all default C defines here, like -D_DEBUG=1
DDEFS =

# List all default ASM defines here, like -D_DEBUG=1
DADEFS =

# List all default directories to look for include files here
DINCDIR =

# List the default directory to look for the libraries here
DLIBDIR =

# List all default libraries here
DLIBS = 

#
# End of default section
##############################################################################

##############################################################################
# Start of user section
#

# List all user C define here, like -D_DEBUG=1
UDEFS = -DARM_MATH_CM4 -D__FPU_PRESENT

# Define ASM defines here
UADEFS =

# List all user directories here
UINCDIR = 

# List the user directory to look for the libraries here
ULIBDIR = .

# List all user libraries here
ULIBS = -lm \
	samples/808bd.o \
	samples/808hatclose.o \
	samples/808hatopen.o \
	samples/808hitom.o \
	samples/808midtom.o \
	samples/808lotom.o \
	samples/808snare.o 

#
# End of user defines
##############################################################################

RULESPATH = $(CHIBIOS)/os/ports/GCC/ARMCMx
include $(RULESPATH)/rules.mk
