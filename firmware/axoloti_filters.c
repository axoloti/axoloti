/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
#include "axoloti_defines.h"
#include "axoloti_filters.h"
#include "axoloti_math.h"
#include "arm_math.h" 

void init_filter_biquad_A(data_filter_biquad_A *d) {
  d->filter_x_n1 = 0;
  d->filter_x_n2 = 0;
  d->filter_y_n1 = 0;
  d->filter_y_n2 = 0;
}

/*
 void f_filter_biquad_A_int64(data_filter_biquad_A *v,int32_t *sourcebuf,int32_t *destbuf,uint32_t filter_W0,uint32_t q_inv) {
 int32_t sinW0 = arm_sin_q31(filter_W0);
 int32_t cosW0 = arm_cos_q31(filter_W0);
 //    int32_t sinW0 = fsini(filter_W0);
 //    int32_t cosW0 = fsini(filter_W0+(INT32_MAX>>2));
 int32_t alpha = ___SMMUL(sinW0,q_inv);
 //    int32_t alpha = sinW0>>8;
 int32_t filter_x_n1 = v->filter_x_n1;
 int32_t filter_x_n2 = v->filter_x_n2;
 int32_t filter_y_n1 = v->filter_y_n1;
 int32_t filter_y_n2 = v->filter_y_n2;
 int32_t filter_b0 = (HALFQ31 - (cosW0>>1))>>SAFESHIFT;
 int32_t filter_b1 = (filter_b0>>1)>>SAFESHIFT;
 int32_t filter_b2 = (filter_b0)>>SAFESHIFT;
 int32_t filter_a0 = (HALFQ31 + alpha)>>SAFESHIFT;
 int32_t filter_a1 = (-(-cosW0))>>SAFESHIFT; // negated
 int32_t filter_a2 = (-(HALFQ31 - alpha))>>SAFESHIFT; // negated
 int64_t filter_a0_inv = (INT64_MAX / filter_a0)>>15;
 int i;
 for(i=0;i<64;i++) {
 int32_t filterinput = *(sourcebuf++);
 int64_t accu = (int64_t)filter_b0*filterinput  \
        + (int64_t)filter_b1*filter_x_n1 \
        + (int64_t)filter_b2*filter_x_n2 \
        + (int64_t)filter_a1*filter_y_n1 \
        + (int64_t)filter_a2*filter_y_n2;
 int32_t filteroutput;
 filteroutput = ((accu>>16)*filter_a0_inv)>>32;
 filter_x_n2 = filter_x_n1;
 filter_x_n1 = filterinput;
 filter_y_n2 = filter_y_n1;
 filter_y_n1 = filteroutput;
 *(destbuf++) = filteroutput;
 }
 v->filter_x_n1 = filter_x_n1;
 v->filter_x_n2 = filter_x_n2;
 v->filter_y_n1 = filter_y_n1;
 v->filter_y_n2 = filter_y_n2;
 }
 */

void f_filter_biquad_A(data_filter_biquad_A *v, const int32_t *sourcebuf,
                       int32_t *destbuf, uint32_t filter_W0, uint32_t q_inv) {
// reference http://www.musicdsp.org/files/Audio-EQ-Cookbook.txt
// LPF
// warning: filter_W0 values above 0x50000000 produce unstable results

  int32_t sinW0; // = arm_sin_q31(filter_W0);
  int32_t cosW0; // = arm_cos_q31(filter_W0);
  int a = filter_W0;
  int b = filter_W0 + (1 << 30);

  SINE2TINTERP(a, sinW0)
  SINE2TINTERP(b, cosW0)

  int32_t alpha = ___SMMUL(sinW0, q_inv);
//    int32_t alpha = sinW0>>8;
  int32_t filter_x_n1 = v->filter_x_n1;
  int32_t filter_x_n2 = v->filter_x_n2;
  int32_t filter_y_n1 = v->filter_y_n1;
  int32_t filter_y_n2 = v->filter_y_n2;
  float filter_a0 = (HALFQ31 + alpha);
  float filter_a0_inv = ((INT32_MAX >> 2) / filter_a0);
  int32_t a0_inv_q31 = (int32_t)(INT32_MAX * filter_a0_inv);
  int32_t filter_a1 = ___SMMUL(-(-cosW0), a0_inv_q31); // negated
  int32_t filter_a2 = ___SMMUL(-(HALFQ31 - alpha), a0_inv_q31); // negated
  int32_t filter_b0 = ___SMMUL(HALFQ31 - (cosW0 >> 1), a0_inv_q31);
  int32_t filter_b1 = (filter_b0 >> 1);
  int i;
  for (i = 0; i < BUFSIZE; i++) {
    int32_t filterinput = *(sourcebuf++);
    int32_t accu = ___SMMUL(filter_b0, filterinput);
    accu = ___SMMLA(filter_b0, filter_x_n2, accu);
    accu = ___SMMLA(filter_b1, filter_x_n1, accu);
    accu = ___SMMLA(filter_a1, filter_y_n1, accu);
    accu = ___SMMLA(filter_a2, filter_y_n2, accu);
    int32_t filteroutput;
    filteroutput = __SSAT(accu, 28) << 4;
    filter_x_n2 = filter_x_n1;
    filter_x_n1 = filterinput;
    filter_y_n2 = filter_y_n1;
    filter_y_n1 = filteroutput;
    *(destbuf++) = filteroutput;
  }
  v->filter_x_n1 = filter_x_n1;
  v->filter_x_n2 = filter_x_n2;
  v->filter_y_n1 = filter_y_n1;
  v->filter_y_n2 = filter_y_n2;
}


