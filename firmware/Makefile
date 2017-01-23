#############################################################################
# Build global options
# NOTE: Can be overridden externally.
#

# Compiler options here.
ifeq ($(USE_OPT),)
  USE_OPT = -O2 -ggdb -fomit-frame-pointer -falign-functions=16 -fno-math-errno -Wno-unused-parameter
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

#Axoloti optional modules, here we define defaults if none supplied
ifeq ($(AXO_OPT_MODULES),)
	AXO_OPT_MODULES = BRAIDS RINGS CLOUDS STREAMS ELEMENTS WARPS
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

CHIBIOS = ${axoloti_release}/chibios


# theoretically should be this...., but CSRC expansion cannot cope with escape spaces
#SPACE :=
#SPACE +=
#FIRMWARE=$(subst $(SPACE),\ ,${axoloti_firmware})
FIRMWARE=.

MUTABLE_INSTRUMENTS = ${FIRMWARE}/mutable_instruments


include board.mk
include $(CHIBIOS)/os/hal/platforms/STM32F4xx/platform.mk
include $(CHIBIOS)/os/hal/hal.mk
include $(CHIBIOS)/os/ports/GCC/ARMCMx/STM32F4xx/port.mk
include $(CHIBIOS)/os/kernel/kernel.mk
	
include $(CHIBIOS)/os/various/fatfs_bindings/fatfs.mk

CMSIS = ${axoloti_release}/CMSIS/


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
       $(CHIBIOS)/os/various/chprintf.c \
       $(CHIBIOS)/os/various/memstreams.c \
       ${FIRMWARE}/axoloti_control.c \
       ${FIRMWARE}/codec.c \
       ${FIRMWARE}/axoloti.c \
       ${FIRMWARE}/axoloti_filters.c \
       ${FIRMWARE}/axoloti_math.c \
       ${FIRMWARE}/axoloti_oscs.c \
       ${FIRMWARE}/axoloti_board.c \
       ${FIRMWARE}/midi.c \
       ${FIRMWARE}/ui.c \
       ${FIRMWARE}/glcdfont.c \
       ${FIRMWARE}/usbcfg.c \
       ${FIRMWARE}/sdcard.c \
       ${FIRMWARE}/pconnection.c \
       ${FIRMWARE}/parameters.c \
       ${FIRMWARE}/main.c \
       ${FIRMWARE}/patch.c \
       ${FIRMWARE}/flash.c \
       ${FIRMWARE}/virtual_control.c \
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
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_cos_q31.c \
       ${CMSIS}/DSP_Lib/Source/FastMathFunctions/arm_sin_q31.c \
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
       ${CMSIS}/DSP_Lib/Source/CommonTables/arm_common_tables.c \
       ${CMSIS}/DSP_Lib/Source/CommonTables/arm_const_structs.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Core/Src/usbh_core.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Core/Src/usbh_ctlreq.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Core/Src/usbh_ioreq.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Core/Src/usbh_pipes.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Src/usbh_hid.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Src/usbh_hid_keybd.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Src/usbh_hid_mouse.c \
       ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Src/usbh_hid_parser.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_ll_usb.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_gpio.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_cortex.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_hcd.c \
       ${FIRMWARE}/usbh_midi_core.c \
       ${FIRMWARE}/usbh_vendor.c \
       ${FIRMWARE}/usbh_conf.c \
       ${FIRMWARE}/serial_midi.c \
       ${FIRMWARE}/bulk_usb.c \
       ${FIRMWARE}/midi_usb.c \
       ${FIRMWARE}/crc32.c \
       ${FIRMWARE}/exceptions.c \
       ${FIRMWARE}/watchdog.c \
       ${FIRMWARE}/sysmon.c \
       ${FIRMWARE}/axoloti_memory.c

#optional modules
ifneq (,$(findstring BRAIDS,$(AXO_OPT_MODULES)))
#$(info including BRAIDS)
BRAIDS_SRC = \
       ${MUTABLE_INSTRUMENTS}/braids/braids_resources.cpp \
       ${MUTABLE_INSTRUMENTS}/braids/analog_oscillator.cpp \
       ${MUTABLE_INSTRUMENTS}/braids/digital_oscillator.cpp \
       ${MUTABLE_INSTRUMENTS}/braids/macro_oscillator.cpp 
endif

ifneq (,$(findstring WARPS,$(AXO_OPT_MODULES)))
#$(info including WARPS)
WARPS_SRC = \
       ${MUTABLE_INSTRUMENTS}/warps/warps_resources.cpp \
       ${MUTABLE_INSTRUMENTS}/warps/dsp/filter_bank.cpp \
       ${MUTABLE_INSTRUMENTS}/warps/dsp/modulator.cpp \
       ${MUTABLE_INSTRUMENTS}/warps/dsp/oscillator.cpp \
       ${MUTABLE_INSTRUMENTS}/warps/dsp/vocoder.cpp 
endif

ifneq (,$(findstring ELEMENTS,$(AXO_OPT_MODULES)))
#$(info including ELEMENTS)
ELEMENTS_SRC = \
       ${FIRMWARE}/axoloti_mi.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/dsp/multistage_envelope.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/dsp/ominous_voice.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/dsp/resonator.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/dsp/string.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/dsp/tube.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/dsp/exciter.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/elements_resources.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/part.cpp \
       ${MUTABLE_INSTRUMENTS}/elements/voice.cpp 
endif

ifneq (,$(findstring CLOUDS,$(AXO_OPT_MODULES)))
#$(info including CLOUDS)
CLOUDS_SRC = \
       ${MUTABLE_INSTRUMENTS}/clouds/clouds_resources.cpp \
       ${MUTABLE_INSTRUMENTS}/clouds/dsp/correlator.cpp \
       ${MUTABLE_INSTRUMENTS}/clouds/dsp/mu_law.cpp \
       ${MUTABLE_INSTRUMENTS}/clouds/dsp/granular_processor.cpp \
       ${MUTABLE_INSTRUMENTS}/clouds/dsp/pvoc/frame_transformation.cpp \
       ${MUTABLE_INSTRUMENTS}/clouds/dsp/pvoc/phase_vocoder.cpp \
       ${MUTABLE_INSTRUMENTS}/clouds/dsp/pvoc/stft.cpp 
endif

ifneq (,$(findstring RINGS,$(AXO_OPT_MODULES)))
#$(info  including RINGS)
RINGS_SRC = \
       ${MUTABLE_INSTRUMENTS}/rings/rings_resources.cpp 
endif

ifneq (,$(findstring STREAMS,$(AXO_OPT_MODULES)))
#$(info including STREAMS)
STREAMS_SRC = \
       ${MUTABLE_INSTRUMENTS}/streams/streams_resources.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/compressor.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/envelope.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/follower.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/lorenz_generator.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/processor.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/svf.cpp \
       ${MUTABLE_INSTRUMENTS}/streams/vactrol.cpp 
endif

#$(info including base MI)

# C++ sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CPPSRC = \
       ${MUTABLE_INSTRUMENTS}/stmlib/dsp/units.cpp \
       ${MUTABLE_INSTRUMENTS}/stmlib/dsp/atan.cpp \
       ${MUTABLE_INSTRUMENTS}/stmlib/utils/random.cpp \
       ${MUTABLE_INSTRUMENTS}/mutable_resources.cpp \
       ${BRAIDS_SRC} \
       ${WARPSS_SRC} \
       ${ELEMENTS_SRC} \
       ${CLOUDS_SRC} \
       ${RINGS_SRC} \
       ${STREAMS_SRC}

 
	   
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
ASMSRC = $(PORTASM) \
         ${CMSIS}/DSP_Lib/Source/TransformFunctions/arm_bitreversal2.s

INCDIR = ${CMSIS}/Include \
         ${CMSIS}/DSP_Lib/Include \
		 $(PORTINC) $(KERNINC) $(TESTINC) \
         $(HALINC) $(PLATFORMINC) $(BOARDINC) \
         $(FATFSINC) \
         $(CHIBIOS)/os/various \
         ${FIRMWARE}/STM32_USB_Host_Library/Core/Inc \
         ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Inc \
         ${FIRMWARE}/STM32F4xx_HAL_Driver/Inc \
         ${MUTABLE_INSTRUMENTS} \
         ${MUTABLE_INSTRUMENTS}/warps \
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
ULIBS = -lm 

#
# End of user defines
##############################################################################

RULESPATH = $(CHIBIOS)/os/ports/GCC/ARMCMx
include $(RULESPATH)/rules.mk
