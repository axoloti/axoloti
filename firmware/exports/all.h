#ifndef EXPORTS_ALL_H
#define EXPORTS_ALL_H

#include "exports/axoloti_math.h"
#include "exports/axoloti_memory.h"
#include "exports/axoloti_oscs.h"
#include "exports/chibios.h"
#include "exports/cmsis_dsplib.h"
#include "exports/codec_adau1961.h"
#include "exports/dlfcn.h"
#include "exports/fatfs.h"
#include "exports/gpio_adc.h"
#include "exports/hal_exports.h"
#include "exports/leds_buttons.h"
#include "exports/libc.h"
#include "exports/logging.h"
#include "exports/midi.h"
#include "exports/parameters.h"
#include "exports/patch.h"

#define API_ALL \
  EXPORTS_AXOLOTI_MATH_SYMBOLS, \
  EXPORTS_AXOLOTI_MEMORY_SYMBOLS, \
  EXPORTS_AXOLOTI_OSCS_SYMBOLS, \
  EXPORTS_CHIBIOS_RT_SYMBOLS, \
  EXPORTS_CMSIS_DSPLIB_SYMBOLS, \
  EXPORTS_CODEC_ADAU1961_SYMBOLS, \
  EXPORTS_DLFCN_SYMBOLS, \
  EXPORTS_FATFS_SYMBOLS, \
  EXPORTS_GPIO_ADC_SYMBOLS, \
  EXPORTS_HAL_SYMBOLS, \
  EXPORTS_LEDS_BUTTONS_SYMBOLS, \
  EXPORTS_LIBC_SYMBOLS, \
  EXPORTS_LOGGING_SYMBOLS, \
  EXPORTS_MIDI_SYMBOLS, \
  EXPORTS_PARAMETERS_SYMBOLS, \
  EXPORTS_PATCH_SYMBOLS, \
  EXPORTS_HAL_SYMBOLS

#endif
