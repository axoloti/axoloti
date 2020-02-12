#ifndef xpatch_h
#define xpatch_h

#include <limits.h>

#define FALSE 0
#define TRUE  1

#include <math.h>
#include "axoloti_filters.h"
#include "axoloti_legacy.h"
#include "axoloti_math.h"
#include "axoloti_memory.h"
#include "axoloti_oscs.h"
#include "axoloti.h"
#include "codec_adau1961.h"
#include "dlfcn.h"
#include "error_codes.h"
#include "gpio_adc.h"
#include "logging.h"
#include "midi.h"
#include "midi_legacy.h"
#include "parameter_functions.h"
#include "parameters.h"
#include "patch.h"
#include "patch_class.h"
#include "stm32f4xx.h"
#include "ch.h"
#include "hal.h"
#include "leds_buttons.h"
#include "fatfs/ff.h"
#include "arm_intrinsics.hpp"
#include "chibios_migration.h"

extern "C" {
  __attribute__((weak)) void  __cxa_pure_virtual(void) {
    // TODO: (low priority) __cxa_pure_virtual report exception
    asm("BKPT 255");
    while(1) {
    }
  }
}

#endif
