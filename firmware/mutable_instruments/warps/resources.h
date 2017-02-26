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
// make -f warps/makefile resources


#ifndef WARPS_RESOURCES_H_
#define WARPS_RESOURCES_H_


#include "stmlib/stmlib.h"



namespace warps {

typedef uint8_t ResourceId;

extern const float* filter_bank_table[];

extern const float* lookup_table_table[];

extern const float* wav_table[];

extern const float fb__87_3000[];
extern const float fb_110_3000[];
extern const float fb_139_3000[];
extern const float fb_175_3000[];
extern const float fb_220_3000[];
extern const float fb_277_3000[];
extern const float fb_349_3000[];
extern const float fb_440_3000[];
extern const float fb_554_3000[];
extern const float fb_698_12000[];
extern const float fb_880_12000[];
extern const float fb_1109_12000[];
extern const float fb_1397_12000[];
extern const float fb_1760_12000[];
extern const float fb_2217_12000[];
extern const float fb_2794_48000[];
extern const float fb_3520_48000[];
extern const float fb_4435_48000[];
extern const float fb_5588_48000[];
extern const float fb_7040_48000[];
extern const float lut_sin[];
extern const float lut_xfade_in[];
extern const float lut_xfade_out[];
extern const float lut_bipolar_fold[];
extern const float lut_midi_to_f_high[];
extern const float lut_midi_to_f_low[];
extern const float lut_pot_curve[];
extern const float lut_ap_poles[];
extern const float wav_sine_i[];
extern const float wav_sine_q[];
extern const float wav_harmonics_i[];
extern const float wav_harmonics_q[];
extern const float wav_buzzy_i[];
extern const float wav_buzzy_q[];
const int FB__87_3000 = 0;
const int FB__87_3000_SIZE = 7;
const int FB_110_3000 = 1;
const int FB_110_3000_SIZE = 7;
const int FB_139_3000 = 2;
const int FB_139_3000_SIZE = 7;
const int FB_175_3000 = 3;
const int FB_175_3000_SIZE = 7;
const int FB_220_3000 = 4;
const int FB_220_3000_SIZE = 7;
const int FB_277_3000 = 5;
const int FB_277_3000_SIZE = 7;
const int FB_349_3000 = 6;
const int FB_349_3000_SIZE = 7;
const int FB_440_3000 = 7;
const int FB_440_3000_SIZE = 7;
const int FB_554_3000 = 8;
const int FB_554_3000_SIZE = 7;
const int FB_698_12000 = 9;
const int FB_698_12000_SIZE = 7;
const int FB_880_12000 = 10;
const int FB_880_12000_SIZE = 7;
const int FB_1109_12000 = 11;
const int FB_1109_12000_SIZE = 7;
const int FB_1397_12000 = 12;
const int FB_1397_12000_SIZE = 7;
const int FB_1760_12000 = 13;
const int FB_1760_12000_SIZE = 7;
const int FB_2217_12000 = 14;
const int FB_2217_12000_SIZE = 7;
const int FB_2794_48000 = 15;
const int FB_2794_48000_SIZE = 7;
const int FB_3520_48000 = 16;
const int FB_3520_48000_SIZE = 7;
const int FB_4435_48000 = 17;
const int FB_4435_48000_SIZE = 7;
const int FB_5588_48000 = 18;
const int FB_5588_48000_SIZE = 7;
const int FB_7040_48000 = 19;
const int FB_7040_48000_SIZE = 7;
const int LUT_SIN = 0;
const int LUT_SIN_SIZE = 1281;
const int LUT_XFADE_IN = 1;
const int LUT_XFADE_IN_SIZE = 257;
const int LUT_XFADE_OUT = 2;
const int LUT_XFADE_OUT_SIZE = 257;
const int LUT_BIPOLAR_FOLD = 3;
const int LUT_BIPOLAR_FOLD_SIZE = 4097;
const int LUT_MIDI_TO_F_HIGH = 4;
const int LUT_MIDI_TO_F_HIGH_SIZE = 256;
const int LUT_MIDI_TO_F_LOW = 5;
const int LUT_MIDI_TO_F_LOW_SIZE = 256;
const int LUT_POT_CURVE = 6;
const int LUT_POT_CURVE_SIZE = 513;
const int LUT_AP_POLES = 7;
const int LUT_AP_POLES_SIZE = 17;
const int WAV_SINE_I = 0;
const int WAV_SINE_I_SIZE = 1025;
const int WAV_SINE_Q = 1;
const int WAV_SINE_Q_SIZE = 1025;
const int WAV_HARMONICS_I = 2;
const int WAV_HARMONICS_I_SIZE = 1025;
const int WAV_HARMONICS_Q = 3;
const int WAV_HARMONICS_Q_SIZE = 1025;
const int WAV_BUZZY_I = 4;
const int WAV_BUZZY_I_SIZE = 1025;
const int WAV_BUZZY_Q = 5;
const int WAV_BUZZY_Q_SIZE = 1025;

}  // namespace warps

#endif  // WARPS_RESOURCES_H_
