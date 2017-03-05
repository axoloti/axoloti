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


#ifndef CLOUDS_RESOURCES_H_
#define CLOUDS_RESOURCES_H_


#include "stmlib/stmlib.h"



namespace clouds {

typedef uint8_t ResourceId;

extern const float* src_filter_table[];

extern const int16_t* lookup_table_int16_table[];

extern const float* lookup_table_table[];

extern const float src_filter_1x_2_31[];
extern const float src_filter_1x_2_45[];
extern const float src_filter_1x_2_63[];
extern const float src_filter_1x_2_91[];
extern const int16_t lut_db[];
extern const float lut_sin[];
extern const float lut_window[];
extern const float lut_xfade_in[];
extern const float lut_xfade_out[];
extern const float lut_sine_window_4096[];
extern const float lut_cutoff[];
extern const float lut_grain_size[];
extern const float lut_quantized_pitch[];
const int SRC_FILTER_1X_2_31=0;
const int SRC_FILTER_1X_2_31_SIZE=31;
const int SRC_FILTER_1X_2_45=1;
const int SRC_FILTER_1X_2_45_SIZE=45;
const int SRC_FILTER_1X_2_63=2;
const int SRC_FILTER_1X_2_63_SIZE=63;
const int SRC_FILTER_1X_2_91=3;
const int SRC_FILTER_1X_2_91_SIZE=91;
const int LUT_DB=0;
const int LUT_DB_SIZE=257;
const int LUT_SIN=0;
const int LUT_SIN_SIZE=1281;
const int LUT_WINDOW=1;
const int LUT_WINDOW_SIZE=4097;
const int LUT_XFADE_IN=2;
const int LUT_XFADE_IN_SIZE=17;
const int LUT_XFADE_OUT=3;
const int LUT_XFADE_OUT_SIZE=17;
const int LUT_SINE_WINDOW_4096=4;
const int LUT_SINE_WINDOW_4096_SIZE=4096;
const int LUT_CUTOFF=5;
const int LUT_CUTOFF_SIZE=257;
const int LUT_GRAIN_SIZE=6;
const int LUT_GRAIN_SIZE_SIZE=257;
const int LUT_QUANTIZED_PITCH=7;
const int LUT_QUANTIZED_PITCH_SIZE=1025;

}  // namespace clouds

#endif  // CLOUDS_RESOURCES_H_
