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
#ifndef PARAMETER_FUNCTIONS_H
#define PARAMETER_FUNCTIONS_H

static void pfun_signed_clamp(ParameterExchange_t *p) {
  p->finalvalue = __SSAT(p->modvalue, 28);
}

static void pfun_unsigned_clamp(ParameterExchange_t *p) {
  p->finalvalue = __USAT(p->modvalue, 27);
}

static void pfun_signed_clamp_fullrange(ParameterExchange_t *p) {
  p->finalvalue = __SSAT(p->modvalue, 28) << 4;
}

static void pfun_unsigned_clamp_fullrange(ParameterExchange_t *p) {
  p->finalvalue = __USAT(p->modvalue, 27) << 4;
}

static void pfun_signed_clamp_squarelaw(ParameterExchange_t *p) {
  int32_t psat = __SSAT(p->modvalue, 28) << 4;
  if (psat > 0)
    p->finalvalue = ___SMMUL(psat, psat) >> 3;
  else
    p->finalvalue = -___SMMUL(psat, psat) >> 3;
}

static void pfun_unsigned_clamp_squarelaw(ParameterExchange_t *p) {
  int32_t psat = __USAT(p->modvalue, 27) << 4;
  p->finalvalue = ___SMMUL(psat, psat) >> 3;
}

static void pfun_signed_clamp_fullrange_squarelaw(ParameterExchange_t *p) {
  int32_t psat = __SSAT(p->modvalue, 28) << 4;
  if (psat > 0)
    p->finalvalue = ___SMMUL(psat, psat) << 1;
  else
    p->finalvalue = -___SMMUL(psat, psat) << 1;
}

static void pfun_unsigned_clamp_fullrange_squarelaw(ParameterExchange_t *p) {
  int32_t psat = __USAT(p->modvalue, 27) << 4;
  p->finalvalue = ___SMMUL(psat, psat) << 1;
}

static void pfun_kexpltime(ParameterExchange_t *p) {
  int32_t in = (-p->modvalue);
  int32_t out;
  MTOF(in, out);
  p->finalvalue = out >> 2;
}

static void pfun_kexpdtime(ParameterExchange_t *p) {
  int32_t in = (-p->modvalue);
  int32_t out;
  MTOF(in, out);
  p->finalvalue = 0x7FFFFFFF - (out >> 2);
}

#endif
