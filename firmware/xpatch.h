extern "C" {
#include <patch.h>
#include <axoloti.h>
#include <parameter_functions.h>
}

#include "arm_intrinsics.hpp"

#include "braids/macro_oscillator.h"
#include "rings/dsp/string.h"
#include "rings/dsp/resonator.h"
#include "rings/dsp/fx/chorus.h"
#include "rings/dsp/fx/ensemble.h"
#include "rings/dsp/fx/reverb.h"
#include "warps/dsp/vocoder.h"
#include "elements/dsp/exciter.h"
#include "elements/dsp/tube.h"
#include "elements/dsp/string.h"
