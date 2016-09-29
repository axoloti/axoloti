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
#include "axoloti_math.h"

#include "math.h"
#include "axoloti_defines.h"

int16_t sinet[SINETSIZE + 1];
int32_t sine2t[SINE2TSIZE + 1];
int16_t windowt[WINDOWSIZE + 1];
uint32_t pitcht[PITCHTSIZE];
uint16_t expt[EXPTSIZE];
uint16_t logt[LOGTSIZE];

void axoloti_math_init(void) {
  volatile short *p;
  volatile uint32_t i;
  p = (short *)sinet;
  for (i = 0; i < SINETSIZE + 1; i++) {
//    volatile float f = i * 2 * PI_F / (float)SINETSIZE;
//    volatile float sin_f = sinf(f);
//    volatile int isinf = (32767.0f*sin_f);
//    float f2 = (sin_f*sin_f*sin_f);
    volatile int q = arm_sin_q31(i << 21);
    *p++ = (int16_t)(q >> 16);
  }

  int32_t *p32 = (int32_t *)sine2t;
  for (i = 0; i < SINE2TSIZE + 1; i++) {
    float f = i * 2 * PI_F / (float)SINE2TSIZE;
    *p32++ = (int32_t)(INT32_MAX * sinf(f));
  }

  p = (short *)windowt;
  for (i = 0; i < WINDOWSIZE + 1; i++) {
    float f = i * 2 * PI_F / (float)WINDOWSIZE;
    *p++ = (int16_t)(32767.0f * (0.5f - 0.5f * cosf(f)));
  }

  uint32_t *q;
  q = (uint32_t *)pitcht;
  for (i = 0; i < PITCHTSIZE; i++) {
    double f = 440.0 * powf(2.0, (i - 69.0 - 64.0) / 12.0);
    double phi = 4.0 * (double)(1 << 30) * f / (SAMPLERATE * 1.0);
    if (phi > ((unsigned int)1 << 31))
      phi = 0x7FFFFFFF;
    *q++ = (uint32_t)phi;
  }

  uint16_t *q16;
  q16 = expt;
  for (i = 0; i < EXPTSIZE; i++) {
    double e = pow(2.0, ((float)i) / (float)EXPTSIZE);
    *q16++ = (uint32_t)(e * (1 + INT16_MAX));
  }

  q16 = logt;
  for (i = 0; i < LOGTSIZE; i++) {
    double e = 0.5 * log(1.0 + ((double)i / (double)LOGTSIZE)) / log(2.0);
    *q16++ = (uint32_t)(e * (1 + INT16_MAX));
  }

  // reset & initialize the hardware random number generator
  RCC->AHB2RSTR |= RCC_AHB2RSTR_RNGRST;
  RCC->AHB2RSTR &= ~RCC_AHB2RSTR_RNGRST;
  RCC->AHB2ENR |= RCC_AHB2ENR_RNGEN;
  chThdSleepMilliseconds(1);
  RNG->CR = RNG_CR_RNGEN;
  while(!(RNG->SR & RNG_SR_DRDY)) {
  }
}

uint32_t FastLog(uint32_t i) {
  Float_t f;
  f.f = i;
  uint32_t r = f.parts.exponent << 23;
  r += f.parts.mantissa >> 10;
  return r;
}
