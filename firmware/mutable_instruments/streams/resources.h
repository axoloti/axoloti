// Copyright 2014 Olivier Gillet.
//
// Author: Olivier Gillet (ol.gillet@gmail.com)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// 
// See http://creativecommons.org/licenses/MIT/ for more information.
//
// -----------------------------------------------------------------------------
//
// Resources definitions.
//
// Automatically generated with:
// make resources


#ifndef STREAMS_RESOURCES_H_
#define STREAMS_RESOURCES_H_


#include "stmlib/stmlib.h"



namespace streams {

typedef uint8_t ResourceId;

extern const char* string_table[];

extern const int16_t* waveforms_table[];

extern const uint16_t* lookup_table_table[];

extern const uint32_t* lookup_table_32_table[];

extern const int16_t wav_gompertz[];
extern const int16_t wav_db[];
extern const uint16_t lut_env_linear[];
extern const uint16_t lut_env_expo[];
extern const uint16_t lut_env_quartic[];
extern const uint16_t lut_square_root[];
extern const uint16_t lut_svf_cutoff[];
extern const uint16_t lut_svf_damp[];
extern const uint16_t lut_2164_gain[];
extern const uint16_t lut_compressor_ratio[];
extern const uint16_t lut_soft_knee[];
extern const uint32_t lut_env_increments[];
extern const uint32_t lut_lp_coefficients[];
extern const uint32_t lut_exp2[];
extern const uint32_t lut_log2[];
extern const uint32_t lut_lorenz_rate[];
const int STR_DUMMY = 0;  // dummy
const int WAV_GOMPERTZ = 0;
const int WAV_GOMPERTZ_SIZE = 1025;
const int WAV_DB = 1;
const int WAV_DB_SIZE = 257;
const int LUT_ENV_LINEAR = 0;
const int LUT_ENV_LINEAR_SIZE = 257;
const int LUT_ENV_EXPO = 1;
const int LUT_ENV_EXPO_SIZE = 257;
const int LUT_ENV_QUARTIC = 2;
const int LUT_ENV_QUARTIC_SIZE = 257;
const int LUT_SQUARE_ROOT = 3;
const int LUT_SQUARE_ROOT_SIZE = 257;
const int LUT_SVF_CUTOFF = 4;
const int LUT_SVF_CUTOFF_SIZE = 257;
const int LUT_SVF_DAMP = 5;
const int LUT_SVF_DAMP_SIZE = 257;
const int LUT_2164_GAIN = 6;
const int LUT_2164_GAIN_SIZE = 257;
const int LUT_COMPRESSOR_RATIO = 7;
const int LUT_COMPRESSOR_RATIO_SIZE = 257;
const int LUT_SOFT_KNEE = 8;
const int LUT_SOFT_KNEE_SIZE = 257;
const int LUT_ENV_INCREMENTS = 0;
const int LUT_ENV_INCREMENTS_SIZE = 257;
const int LUT_LP_COEFFICIENTS = 1;
const int LUT_LP_COEFFICIENTS_SIZE = 640;
const int LUT_EXP2 = 2;
const int LUT_EXP2_SIZE = 257;
const int LUT_LOG2 = 3;
const int LUT_LOG2_SIZE = 257;
const int LUT_LORENZ_RATE = 4;
const int LUT_LORENZ_RATE_SIZE = 257;

}  // namespace streams

#endif  // STREAMS_RESOURCES_H_
