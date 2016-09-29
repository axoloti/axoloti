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
#ifndef _AXOLOTI_FILTERS_H
#define _AXOLOTI_FILTERS_H

#define HALFQ31 (1<<30)
#include "axoloti_defines.h"
#include "axoloti_math.h"

typedef struct {
  int32_t filter_x_n1;
  int32_t filter_x_n2;
  int32_t filter_y_n1;
  int32_t filter_y_n2;
} data_filter_biquad_A;

#define process_filter_biquad_A(name, sourcebuf, destbuf, filter_W0, q_inv) \
  f_filter_biquad_A(name,sourcebuf,destbuf,filter_W0,q_inv);

void init_filter_biquad_A(data_filter_biquad_A *d);
void f_filter_biquad_A(data_filter_biquad_A *v, const int32_t *sourcebuf,
                       int32_t *destbuf, uint32_t filter_W0, uint32_t q_inv);

typedef struct {
  int32_t filter_x_n1;
  int32_t filter_x_n2;
  int32_t filter_y_n1;
  int32_t filter_y_n2;
} biquad_state;

/* biquad_coefficients : Direct Form 1 coefficients
 *
 * y[n] = (b0/a0)*x[n] + (b1/a0)*x[n-1] + (b2/a0)*x[n-2]
 *                     - (a1/a0)*y[n-1] - (a2/a0)*y[n-2]
 */

typedef struct {
  int32_t cyn_1; // coefficient of y[n-1]
  int32_t cyn_2; // coefficient of y[n-2]
  int32_t cxn_0; // coefficient of x[n]
  int32_t cxn_1; // coefficient of x[n-1]
  int32_t cxn_2; // coefficient of x[n-2]
} biquad_coefficients;

static __attribute__ ((noinline)) void biquad_clearstate(biquad_state *state) {
  state->filter_x_n1 = 0;
  state->filter_x_n2 = 0;
  state->filter_y_n1 = 0;
  state->filter_y_n2 = 0;
}

static __attribute__ ((noinline)) void biquad_dsp(biquad_state *state,
                                                  biquad_coefficients *coefs,
                                                  const int32buffer inbuffer,
                                                  int32buffer outbuffer) {
  int32_t filter_x_n1 = state->filter_x_n1;
  int32_t filter_x_n2 = state->filter_x_n2;
  int32_t filter_y_n1 = state->filter_y_n1;
  int32_t filter_y_n2 = state->filter_y_n2;
  int i;
  for (i = 0; i < BUFSIZE; i++) {
    int32_t filterinput = inbuffer[i];
    int32_t accu = ___SMMUL(coefs->cxn_0, filterinput);
    accu = ___SMMLA(coefs->cxn_1, filter_x_n1, accu);
    accu = ___SMMLA(coefs->cxn_2, filter_x_n2, accu);
    accu = ___SMMLS(coefs->cyn_1, filter_y_n1, accu);
    accu = ___SMMLS(coefs->cyn_2, filter_y_n2, accu);
    int32_t filteroutput;
    filteroutput = accu << 4;
    filter_x_n2 = filter_x_n1;
    filter_x_n1 = filterinput;
    filter_y_n2 = filter_y_n1;
    filter_y_n1 = filteroutput;
    outbuffer[i] = __SSAT(filteroutput, 28);
  }
  state->filter_x_n1 = filter_x_n1;
  state->filter_x_n2 = filter_x_n2;
  state->filter_y_n1 = filter_y_n1;
  state->filter_y_n2 = filter_y_n2;
}

static __attribute__ ((noinline)) void biquad_lp_coefs(
    biquad_coefficients *coefs, uint32_t filter_W0, uint32_t q_inv) {
  filter_W0 = filter_W0 >> 1;
  int32_t sinW0 = arm_sin_q31(filter_W0);
  int32_t cosW0 = arm_cos_q31(filter_W0);
//    int32_t sinW0 = fsini(filter_W0);
//    int32_t cosW0 = fsini(filter_W0+(INT32_MAX>>2));
  int32_t alpha = ___SMMUL(sinW0, q_inv);
//    int32_t alpha = sinW0>>8;
  float filter_a0 = (HALFQ31 + alpha);
  float filter_a0_inv = ((INT32_MAX >> 2) / filter_a0);
  int32_t a0_inv_q31 = (int32_t)(INT32_MAX * filter_a0_inv);
  coefs->cyn_1 = ___SMMUL((-cosW0), a0_inv_q31);
  coefs->cyn_2 = ___SMMUL((HALFQ31 - alpha), a0_inv_q31);
  coefs->cxn_0 = ___SMMUL(___SMMUL(HALFQ31 - (cosW0 >> 1), a0_inv_q31), q_inv);
  coefs->cxn_1 = coefs->cxn_0 << 1;
  coefs->cxn_2 = coefs->cxn_0;
}

/* biquad_coefficients : Direct Form 1 coefficients
 *
 * y[n] = (b0/a0)*x[n] + (b1/a0)*x[n-1] + (b2/a0)*x[n-2]
 *                     - (a1/a0)*y[n-1] - (a2/a0)*y[n-2]
 *
 typedef struct {
 int32_t cyn_1; // coefficient of y[n-1] = a1/a0
 int32_t cyn_2; // coefficient of y[n-2] = a2/a0
 int32_t cxn_0; // coefficient of x[n]   = b0/a0
 int32_t cxn_1; // coefficient of x[n-1] = b1/a0
 int32_t cxn_2; // coefficient of x[n-2] = b2/a0
 } biquad_coefficients;
 */

static void __attribute__ ((noinline)) biquad_bp_coefs(biquad_coefficients *coefs,
                                                uint32_t filter_W0,
                                                uint32_t q_inv) {
//  (constant 0 dB peak gain)
  filter_W0 = filter_W0 >> 1;
  int32_t sinW0 = arm_sin_q31(filter_W0);
  int32_t cosW0 = arm_cos_q31(filter_W0);
//    int32_t sinW0 = fsini(filter_W0);
//    int32_t cosW0 = fsini(filter_W0+(INT32_MAX>>2));
  int32_t alpha = ___SMMUL(sinW0, q_inv);
//    int32_t alpha = sinW0>>8;
  float filter_a0 = (HALFQ31 + alpha);
  float filter_a0_inv = ((INT32_MAX >> 2) / filter_a0);
  int32_t a0_inv_q31 = (int32_t)(INT32_MAX * filter_a0_inv);
  coefs->cyn_1 = ___SMMUL((-cosW0), a0_inv_q31);
  coefs->cyn_2 = ___SMMUL((HALFQ31 - alpha), a0_inv_q31);
  coefs->cxn_0 = ___SMMUL(alpha, a0_inv_q31);
  coefs->cxn_1 = 0;
  coefs->cxn_2 = -coefs->cxn_0;
}

static void __attribute__ ((noinline)) biquad_hp_coefs(biquad_coefficients *coefs,
                                                uint32_t filter_W0,
                                                uint32_t q_inv) {
  filter_W0 = filter_W0 >> 1;
  int32_t sinW0 = arm_sin_q31(filter_W0);
  int32_t cosW0 = arm_cos_q31(filter_W0);
//    int32_t sinW0 = fsini(filter_W0);
//    int32_t cosW0 = fsini(filter_W0+(INT32_MAX>>2));
  int32_t alpha = ___SMMUL(sinW0, q_inv);
//    int32_t alpha = sinW0>>8;
  float filter_a0 = (HALFQ31 + alpha);
  float filter_a0_inv = ((INT32_MAX >> 2) / filter_a0);
  int32_t a0_inv_q31 = (int32_t)(INT32_MAX * filter_a0_inv);
  coefs->cyn_1 = ___SMMUL((-cosW0), a0_inv_q31);
  coefs->cyn_2 = ___SMMUL((HALFQ31 - alpha), a0_inv_q31);
  coefs->cxn_0 = ___SMMUL(___SMMUL(HALFQ31 + (cosW0 >> 1), a0_inv_q31), q_inv);
  coefs->cxn_1 = -(coefs->cxn_0 << 1);
  coefs->cxn_2 = coefs->cxn_0;
}

#endif
