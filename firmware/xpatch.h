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

#define SECTION_SDRAM_DATA __attribute__ ((section ( ".sdramdata" )))
#define SECTION_SDRAM_BSS __attribute__ ((section ( ".sdram" )))
#define SECTION_DMADATA __attribute__ ((section ( ".sram2" )))
