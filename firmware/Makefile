##############################################################################
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

# Enable this if you want the linker to remove unused code and data.
ifeq ($(USE_LINK_GC),)
  USE_LINK_GC = yes
endif

# Linker extra options here.
ifeq ($(USE_LDOPT),)
  USE_LDOPT = --print-memory-usage
endif

# Enable this if you want link time optimizations (LTO).
ifeq ($(USE_LTO),)
  USE_LTO = yes
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
  USE_PROCESS_STACKSIZE = 0x450
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

#
# Architecture or project specific options
##############################################################################

##############################################################################
# Project, target, sources and paths
#

# Define project name here
PROJECT = axoloti

# Target settings.
MCU  = cortex-m4

# Imported source files and paths
axoloti_runtime ?= ..
axoloti_release ?= ..
axoloti_home ?= ..
axoloti_firmware ?= ../firmware

include $(axoloti_firmware)/ch_version.mk

# theoretically should be this...., but CSRC expansion cannot cope with escape spaces
#SPACE :=
#SPACE +=
#FIRMWARE=$(subst $(SPACE),\ ,${axoloti_firmware})
FIRMWARE=.

# Licensing files.
include $(CHIBIOS)/os/license/license.mk
# Startup files.
include $(CHIBIOS)/os/common/startup/ARMCMx/compilers/GCC/mk/startup_stm32f4xx.mk
# HAL-OSAL files (optional).
include $(CHIBIOS_CONTRIB)/os/hal/hal.mk
include $(CHIBIOS_CONTRIB)/os/hal/ports/STM32/STM32F4xx/platform.mk
include board.mk
include $(CHIBIOS)/os/hal/osal/rt/osal.mk
# RTOS files (optional).
include $(CHIBIOS)/os/rt/rt.mk
include $(CHIBIOS)/os/common/ports/ARMCMx/compilers/GCC/mk/port_v7m.mk
# FATFS
include $(CHIBIOS)/os/various/fatfs_bindings/fatfs.mk
# streams
include $(CHIBIOS)/os/hal/lib/streams/streams.mk

# get the same binary as openOCD flashes from elf
override BIN = $(CP) -O binary --gap-fill 0xff
override HEX = $(CP) -O ihex --gap-fill 0xff

CMSIS = ${FIRMWARE}/../api/CMSIS/

# Define linker script file here
LDSCRIPT= STM32F427xG.ld

# C sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CSRC = $(ALLCSRC) \
       ${FIRMWARE}/elfloader/loader.c \
       ${FIRMWARE}/ch_version_check.c \
       ${FIRMWARE}/axoloti_control.c \
       ${FIRMWARE}/codec.c \
       ${FIRMWARE}/axoloti_math.c \
       ${FIRMWARE}/axoloti_oscs.c \
       ${FIRMWARE}/axoloti_board.c \
       ${FIRMWARE}/dlfcn.c \
       ${FIRMWARE}/error_codes.c \
       ${FIRMWARE}/fatfs_dmafix.c \
       ${FIRMWARE}/midi.c \
       ${FIRMWARE}/midi_routing.c \
       ${FIRMWARE}/midi_usb.c \
       ${FIRMWARE}/ui.c \
       ${FIRMWARE}/glcdfont.c \
       ${FIRMWARE}/usbcfg.c \
       ${FIRMWARE}/sdcard.c \
       ${FIRMWARE}/sdram.c \
       ${FIRMWARE}/pconnection.c \
       ${FIRMWARE}/pconn_extra.c \
       ${FIRMWARE}/parameters.c \
       ${FIRMWARE}/exports.c \
       ${FIRMWARE}/main.c \
       ${FIRMWARE}/patch.c \
       ${FIRMWARE}/flash.c \
       ${FIRMWARE}/firmware_chunks.c \
       ${FIRMWARE}/midi_buffer.c \
       ${FIRMWARE}/midi_decoder.c \
       ${FIRMWARE}/midi_din.c \
       ${FIRMWARE}/midi_encoder.c \
       ${FIRMWARE}/midi_gpio.c \
       ${FIRMWARE}/midi_usbh.c \
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
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_i2c.c \
       ${FIRMWARE}/STM32F4xx_HAL_Driver/Src/stm32f4xx_hal_hcd.c \
       ${FIRMWARE}/usbh_midi_core.c \
       ${FIRMWARE}/usbh_vendor.c \
       ${FIRMWARE}/usbh_conf.c \
       ${FIRMWARE}/spilink.c \
       ${FIRMWARE}/spidb.c \
       ${FIRMWARE}/crc32.c \
       ${FIRMWARE}/exceptions.c \
       ${FIRMWARE}/sysmon.c \
       ${FIRMWARE}/axoloti_memory.c \
       ${FIRMWARE}/semihosting_stream.c \
       ${FIRMWARE}/dbg_stream.c \
       ${FIRMWARE}/null_stream.c \
       ${FIRMWARE}/qgfx.c \
       ${FIRMWARE}/ui_nodes/action_function.c \
       ${FIRMWARE}/ui_nodes/int_value.c \
       ${FIRMWARE}/ui_nodes/node_list.c \
       ${FIRMWARE}/ui_nodes/object_list.c \
       ${FIRMWARE}/ui_nodes/object.c \
       ${FIRMWARE}/ui_nodes/param_list.c \
       ${FIRMWARE}/ui_nodes/param.c \
       ${FIRMWARE}/ui_nodes/short_value.c \
       ${FIRMWARE}/ui_nodes/ui_nodes/ui_nodes_common.c \
       ${FIRMWARE}/menu_content/adc_menu.c \
       ${FIRMWARE}/menu_content/food_menu.c \
       ${FIRMWARE}/menu_content/loadpatch_menu.c \
       ${FIRMWARE}/menu_content/main_menu.c \
       ${FIRMWARE}/menu_content/midi_clock_menu.c \
       ${FIRMWARE}/menu_content/midi_controller.c \
       ${FIRMWARE}/menu_content/midi_monitor.c \
       ${FIRMWARE}/menu_content/sdcard_menu.c \
       ${FIRMWARE}/menu_content/processes_menu.c \
       ${FIRMWARE}/ui_nodes/test_menu.c \
       ${FIRMWARE}/vfile_ops/vfile_fatfs.c \
       ${FIRMWARE}/vfile_ops/vfile_mem.c

# C++ sources that can be compiled in ARM or THUMB mode depending on the global
# setting.
CPPSRC = \
      ${FIRMWARE}/patch_wrapper.cpp

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
ASMSRC =

ASMXSRC = $(STARTUPASM) $(PORTASM) $(OSALASM)

INCDIR = $(CONFDIR) $(ALLINC) $(TESTINC) \
         $(CMSIS)/Core/Include \
         ${CMSIS}/DSP/Include \
         $(FATFSINC) \
         $(CHIBIOS) \
         ${FIRMWARE}/STM32F4xx_HAL_Driver/Inc \
         ${FIRMWARE}/../api \
         ${FIRMWARE}/STM32_USB_Host_Library/Core/Inc \
         ${FIRMWARE}/STM32_USB_Host_Library/Class/HID/Inc \
         ${FIRMWARE}/STM32F4xx_HAL_Driver/Inc
#
# Project, target, sources and paths
##############################################################################

##############################################################################
# Start of user section
#

# List all user C define here, like -D_DEBUG=1
UDEFS = -DARM_MATH_CM4 -DSTM32F427xx -D__FPU_PRESENT=1U

# Define ASM defines here
UADEFS =

# List all user directories here
UINCDIR = api

# List the user directory to look for the libraries here
ULIBDIR = .

# List all user libraries here
ULIBS = --specs=nosys.specs -lm $(CMSIS)/DSP/Lib/GCC/libarm_cortexM4lf_math.a
# to enable semihosting, use --specs=rdimon.specs

#
# End of user section
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
DEVICE = "unknown_device"
# TODO: DEVICE should probably not be in DEFS

API = ${FIRMWARE}/../api
API_FATFS = $(API)/fatfs
API_STM32f4XX = $(API)/stm32f4xx

ch_header:
	@echo Merging headers
	@$(CC) -I./genapi/dummy $(IINCDIR) -D$(DEVICE) $(DEFS) -nostdinc -include cmparams.h -include _delimiter.h -fdirectives-only -dD -E -P $(CHIBIOS)/os/rt/include/ch.h -o tmpch1.h
	@$(CC) -fpreprocessed -dD -E -P tmpch1.h -o tmpch2.h
	@sed -e '1,/___HEADER_BEGIN___/d' tmpch2.h > tmpch3.h
	@echo "#ifndef _CHIBIOS_RT_" > api/ch.h
	@cat ./genapi/dummy/_header.h >> api/ch.h
	@cat tmpch3.h >> api/ch.h
	@cat ./genapi/dummy/_footer.h >> api/ch.h
	@rm tmpch*.h
	@echo Done

hal_header:
	@echo Merging headers
	@$(CC) -I./genapi -I./genapi/dummy $(IINCDIR) -D$(DEVICE) $(DEFS) -nostdinc -include cmparams.h -include _delimiter.h -fdirectives-only -dD -E -P genapi/hal_exports.h -o tmphal1.h
	@$(CC) -fpreprocessed -dD -E -P tmphal1.h -o tmphal2.h
	@sed -e '1,/___HEADER_BEGIN___/d' tmphal2.h > tmphal3.h
	@echo "#ifndef _CHIBIOS_HAL_" > $(API)/hal.h
	@cat ./genapi/dummy/_header.h >> $(API)/hal.h
	@cat tmphal3.h >> $(API)/hal.h
	@cat ./genapi/dummy/_footer.h >> $(API)/hal.h
	@rm tmphal*.h
	@echo Done

fatfs_api:
	@echo "copying fatfs api"
	@cp $(CHIBIOS)/ext/fatfs/src/ff.h $(API_FATFS)
	@cp $(CHIBIOS)/ext/fatfs/src/integer.h $(API_FATFS)
	@cp ${FIRMWARE}/ffconf.h $(API_FATFS)

stm32f4xx_api:
	@echo "copying stm32f4xx api"
	@cp $(CHIBIOS)/os/common/ext/ST/STM32F4xx/stm32f427xx.h $(API_STM32f4XX)
	@cp $(CHIBIOS)/os/common/ext/ST/STM32F4xx/system_stm32f4xx.h $(API_STM32f4XX)
	@cp $(CHIBIOS)/os/common/ext/ST/STM32F4xx/stm32f4xx.h $(API_STM32f4XX)
	@cp $(CHIBIOS)/os/common/startup/ARMCMx/devices/STM32F4xx/cmparams.h $(API_STM32f4XX)

api: ch_header hal_header fatfs_api stm32f4xx_api

#
# Custom rules
##############################################################################
