#############################################################################
# Build global options
# NOTE: Can be overridden externally.
#

# Compiler options here.
ifeq ($(USE_OPT),)
  USE_OPT = -O2 -ggdb -fomit-frame-pointer -falign-functions=16 -fno-math-errno
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

# Linker extra options here.
ifeq ($(USE_LDOPT),)
  USE_LDOPT = --print-memory-usage
endif

# Enable this if you want link time optimizations (LTO)
ifeq ($(USE_LTO),)
  USE_LTO = no
endif

# If enabled, this option allows to compile the application in THUMB mode.
ifeq ($(USE_THUMB),)
  USE_THUMB = yes
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

# Enables the use of FPU on Cortex-M4 (no, softfp, hard).
ifeq ($(USE_FPU),)
  USE_FPU = hard
endif
AXO_OPT_MODULES =

#Axoloti optional modules, here we define defaults if none supplied
ifeq ($(AXO_OPT_MODULES),)
	# AXO_OPT_MODULES = BRAIDS RINGS STREAMS ELEMENTS CLOUDS WARPS 
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
axoloti_runtime ?= ..
axoloti_release ?= ..
axoloti_home ?= ..
axoloti_firmware ?= ../firmware

CHIBIOS = ${axoloti_release}/ChibiOS_16.1.8
CHIBIOS_CONTRIB = $(CHIBIOS)/community


# theoretically should be this...., but CSRC expansion cannot cope with escape spaces
#SPACE :=
#SPACE +=
#FIRMWARE=$(subst $(SPACE),\ ,${axoloti_firmware})
FIRMWARE=.

MUTABLE_INSTRUMENTS = ${FIRMWARE}/mutable_instruments


include board.mk
include $(CHIBIOS)/os/common/ports/ARMCMx/compilers/GCC/mk/startup_stm32f4xx.mk
# HAL-OSAL files (optional).
include $(CHIBIOS_CONTRIB)/os/hal/hal.mk
include $(CHIBIOS_CONTRIB)/os/hal/ports/STM32/STM32F4xx/platform.mk
include $(CHIBIOS)/os/hal/osal/rt/osal.mk
# RTOS files (optional).
include $(CHIBIOS)/os/rt/rt.mk
include $(CHIBIOS)/os/rt/ports/ARMCMx/compilers/GCC/mk/port_v7m.mk
	
include $(CHIBIOS)/os/various/fatfs_bindings/fatfs.mk

CMSIS = ${axoloti_release}/CMSIS/


# Define linker script file here
LDSCRIPT= STM32F427xG.ld

CMSIS_SRC = \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_fast_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/FilteringFunctions/arm_biquad_cascade_df1_q31.c

CMSIS_SRC1 = \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_bitreversal.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix2_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix2_init_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix2_init_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix2_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix2_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix2_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_init_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_init_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix4_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_cfft_radix8_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_fast_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_fast_init_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_init_f32.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_init_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_init_q31.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_q15.c \
       ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_rfft_q31.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_mult_q31.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_mult_q15.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_mult_f32.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_shift_q31.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_shift_q15.c \
       ${CMSIS}/DSP_Lib/Source/BasicMathFunctions/arm_scale_f32.c \
       ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mult_cmplx_q31.c \
	   ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mult_cmplx_f32.c \
       ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mag_q31.c \
       ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mult_cmplx_q15.c \
       ${CMSIS}/DSP_Lib/Source/ComplexMathFunctions/arm_cmplx_mag_q15.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_sqrt_q31.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_sqrt_q15.c \
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
       ${CMSIS}/DSP_Lib/Source/CommonTables/arm_const_structs.c

# C sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CSRC = $(STARTUPSRC) \
       $(KERNSRC) \
       $(PORTSRC) \
       $(OSALSRC) \
       $(HALSRC) \
       $(PLATFORMSRC) \
       $(BOARDSRC) \
       $(FATFSSRC) \
       $(CHIBIOS)/os/hal/lib/streams/chprintf.c \
       $(CHIBIOS)/os/hal/lib/streams/memstreams.c \
       ${FIRMWARE}/axoloti_control.c \
       ${FIRMWARE}/codec.c \
       ${FIRMWARE}/axoloti.c \
       ${FIRMWARE}/axoloti_filters.c \
       ${FIRMWARE}/axoloti_math.c \
       ${FIRMWARE}/axoloti_oscs.c \
       ${FIRMWARE}/axoloti_board.c \
       ${FIRMWARE}/midi.c \
       ${FIRMWARE}/midi_usb.c \
       ${FIRMWARE}/ui.c \
       ${FIRMWARE}/glcdfont.c \
       ${FIRMWARE}/usbcfg.c \
       ${FIRMWARE}/sdcard.c \
       ${FIRMWARE}/sdram.c \
       ${FIRMWARE}/pconnection.c \
       ${FIRMWARE}/parameters.c \
       ${FIRMWARE}/patch_chunks.c \
       ${FIRMWARE}/main.c \
       ${FIRMWARE}/patch.c \
       ${FIRMWARE}/flash.c \
       ${FIRMWARE}/firmware_chunks.c \
       ${FIRMWARE}/midi_buffer.c \
       ${FIRMWARE}/midi_usbh.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_cos_q31.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_sin_q31.c \
       ${CMSIS}/DSP_Lib/Source/CommonTables/arm_common_tables.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_gpio.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_cortex.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_i2c.c \
       ${FIRMWARE}/usbh_midi_class.c \
       ${FIRMWARE}/usbh_virus_class.c \
       ${FIRMWARE}/usbh_conf.c \
       ${FIRMWARE}/serial_midi.c \
       ${FIRMWARE}/spilink.c \
       ${FIRMWARE}/spidb.c \
       ${FIRMWARE}/crc32.c \
       ${FIRMWARE}/exceptions.c \
       ${FIRMWARE}/watchdog.c \
       ${FIRMWARE}/sysmon.c \
       ${FIRMWARE}/axoloti_memory.c \
       ${FIRMWARE}/qgfx.c \
       ${FIRMWARE}/ui_nodes/action_function.c \
       ${FIRMWARE}/ui_nodes/int_value.c \
       ${FIRMWARE}/ui_nodes/node_list.c \
       ${FIRMWARE}/ui_nodes/object_list.c \
       ${FIRMWARE}/ui_nodes/object.c \
       ${FIRMWARE}/ui_nodes/param_list.c \
       ${FIRMWARE}/ui_nodes/param.c \
       ${FIRMWARE}/ui_nodes/short_value.c \
       ${FIRMWARE}/menu_content/adc_menu.c \
       ${FIRMWARE}/menu_content/food_menu.c \
       ${FIRMWARE}/menu_content/loadpatch_menu.c \
       ${FIRMWARE}/menu_content/main_menu.c \
       ${FIRMWARE}/menu_content/midi_clock.c \
       ${FIRMWARE}/menu_content/midi_controller.c \
       ${FIRMWARE}/menu_content/midi_monitor.c \
       ${FIRMWARE}/menu_content/sdcard_menu.c \
       ${FIRMWARE}/menu_content/processes_menu.c \
       ${FIRMWARE}/ui_nodes/test_menu.c \
       $(CMSIS_SRC)


#$(info including base MI)


# C++ sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CPPSRC = \
       ${MUTABLE_INSTRUMENTS}/stmlib/dsp/units.cpp \
       ${MUTABLE_INSTRUMENTS}/stmlib/dsp/atan.cpp \
       ${MUTABLE_INSTRUMENTS}/stmlib/utils/random.cpp 

 
	   
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
ASMSRC = $(STARTUPASM) $(PORTASM) $(OSALASM) \
         ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_bitreversal2.s

INCDIR = ${CMSIS}/Include \
         ${CMSIS}/DSP_Lib/Include \
		 $(STARTUPINC) $(KERNINC) $(PORTINC) $(OSALINC) \
         $(HALINC) $(PLATFORMINC) $(BOARDINC) $(TESTINC) \
         $(FATFSINC) \
         $(CHIBIOS)/os/various \
         $(CHIBIOS)/os/hal/lib/streams \
         ${FIRMWARE}/STM32_USB_Host_Library/Core/Inc \
         ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Inc \
         ${FIRMWARE}/STM32F4xx_HAL_Driver/Inc \
         ${MUTABLE_INSTRUMENTS} \
         ${MUTABLE_INSTRUMENTS}/braids \
         ${MUTABLE_INSTRUMENTS}/rings \
         ${MUTABLE_INSTRUMENTS}/elements \
         ${MUTABLE_INSTRUMENTS}/stmlib

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
CWARN = -Wall -Wextra -Wstrict-prototypes -Wno-unused-parameter -Wno-strict-aliasing

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
ULIBS = -lm 

#
# End of user defines
##############################################################################

RULESPATH = $(CHIBIOS)/os/common/ports/ARMCMx/compilers/GCC
include $(RULESPATH)/rules.mk
