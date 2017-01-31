extern "C" {
#include <patch.h>
#include <axoloti.h>
#include <parameter_functions.h>
#include <axoloti_memory.h>
}

#include "arm_intrinsics.hpp"

#include "braids/parameter_interpolation.h"
#include "braids/resources.h"
#include "rings/dsp/string.h"
#include "rings/dsp/resonator.h"
#include "warps/dsp/modulator.h"
#include "warps/dsp/vocoder.h"
#include "elements/dsp/exciter.h"
#include "elements/dsp/tube.h"
#include "elements/dsp/string.h"
#include "elements/dsp/fx/diffuser.h"
#include "elements/dsp/fx/reverb.h"
#include "clouds/dsp/granular_processor.h"
#include "elements/dsp/part.h"
#include "elements/dsp/patch.h"
#include "streams/processor.h"
