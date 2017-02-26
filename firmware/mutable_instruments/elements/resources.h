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


#ifndef ELEMENTS_RESOURCES_H_
#define ELEMENTS_RESOURCES_H_


#include "stmlib/stmlib.h"
#include "../mutable_resources.h"
using namespace mutable_resources;

namespace elements {

typedef uint8_t ResourceId;

extern const int16_t* lookup_table_int16_table[];

extern const uint32_t* lookup_table_uint32_table[];

extern const float* lookup_table_table[];

//extern const int16_t* sample_table[];
extern int16_t* sample_table[]; //Axoloti

extern const size_t* sample_boundary_table[];

extern const int16_t lut_db_led_brightness[];
extern const float lut_approx_svf_gain[];
extern const float lut_approx_svf_g[];
extern const float lut_approx_svf_r[];
extern const float lut_approx_svf_h[];
extern const float lut_accent_gain_coarse[];
extern const float lut_accent_gain_fine[];
extern const float lut_env_increments[];
extern const float lut_env_linear[];
extern const float lut_env_expo[];
extern const float lut_env_quartic[];
extern const float lut_midi_to_f_high[];
extern const float lut_midi_to_increment_high[];
extern const float lut_midi_to_f_low[];
extern const float lut_detune_quantizer[];
extern const int16_t smp_sample_data[];
extern const int16_t smp_noise_sample[];
extern const size_t smp_boundaries[];
const int LUT_DB_LED_BRIGHTNESS = 0;
const int LUT_DB_LED_BRIGHTNESS_SIZE = 513;
const int LUT_SINE = 0;
const int LUT_SINE_SIZE = 4097;
const int LUT_APPROX_SVF_GAIN = 1;
const int LUT_APPROX_SVF_GAIN_SIZE = 257;
const int LUT_APPROX_SVF_G = 2;
const int LUT_APPROX_SVF_G_SIZE = 257;
const int LUT_APPROX_SVF_R = 3;
const int LUT_APPROX_SVF_R_SIZE = 257;
const int LUT_APPROX_SVF_H = 4;
const int LUT_APPROX_SVF_H_SIZE = 257;
const int LUT_4_DECADES = 5;
const int LUT_4_DECADES_SIZE = 257;
const int LUT_ACCENT_GAIN_COARSE = 6;
const int LUT_ACCENT_GAIN_COARSE_SIZE = 257;
const int LUT_ACCENT_GAIN_FINE = 7;
const int LUT_ACCENT_GAIN_FINE_SIZE = 257;
const int LUT_STIFFNESS = 8;
const int LUT_STIFFNESS_SIZE = 257;
const int LUT_ENV_INCREMENTS = 9;
const int LUT_ENV_INCREMENTS_SIZE = 258;
const int LUT_ENV_LINEAR = 10;
const int LUT_ENV_LINEAR_SIZE = 258;
const int LUT_ENV_EXPO = 11;
const int LUT_ENV_EXPO_SIZE = 258;
const int LUT_ENV_QUARTIC = 12;
const int LUT_ENV_QUARTIC_SIZE = 258;
const int LUT_MIDI_TO_F_HIGH = 13;
const int LUT_MIDI_TO_F_HIGH_SIZE = 256;
const int LUT_MIDI_TO_INCREMENT_HIGH = 14;
const int LUT_MIDI_TO_INCREMENT_HIGH_SIZE = 256;
const int LUT_MIDI_TO_F_LOW = 15;
const int LUT_MIDI_TO_F_LOW_SIZE = 256;
const int LUT_FM_FREQUENCY_QUANTIZER = 16;
const int LUT_FM_FREQUENCY_QUANTIZER_SIZE = 129;
const int LUT_DETUNE_QUANTIZER = 17;
const int LUT_DETUNE_QUANTIZER_SIZE = 65;
const int LUT_SVF_SHIFT = 18;
const int LUT_SVF_SHIFT_SIZE = 257;
const int SMP_SAMPLE_DATA = 0;
const int SMP_SAMPLE_DATA_SIZE = 128013;
const int SMP_NOISE_SAMPLE = 1;
const int SMP_NOISE_SAMPLE_SIZE = 40963;
const int SMP_BOUNDARIES = 0;
const int SMP_BOUNDARIES_SIZE = 10;

}  // namespace elements

#endif  // ELEMENTS_RESOURCES_H_
