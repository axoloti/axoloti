extern "C" {
#include <patch.h>
#include <axoloti.h>
#include <parameter_functions.h>
#include <axoloti_memory.h>
#include <glcdfont.h>
#include <chprintf.h>
#include <string.h>
}

#include "patch_chunks.h"

#include "arm_intrinsics.hpp"
#include "braids/parameter_interpolation.h"
#include "braids/resources.h"
#include "rings/dsp/string.h"
#include "rings/dsp/resonator.h"
//#include "warps/dsp/modulator.h"
//#include "warps/dsp/vocoder.h"
// #include "elements/dsp/exciter.h"
// #include "elements/dsp/tube.h"
// #include "elements/dsp/string.h"
// #include "elements/dsp/fx/diffuser.h"
// #include "elements/dsp/fx/reverb.h"
// #include "elements/dsp/part.h"
// #include "elements/dsp/patch.h"
#include "streams/processor.h"

#define SECTION_SDRAM_DATA __attribute__ ((section ( ".sdramdata" )))
#define SECTION_SDRAM_BSS __attribute__ ((section ( ".sdram" )))
#define SECTION_DMADATA __attribute__ ((section ( ".sram2" )))
