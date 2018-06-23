/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
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

// NEW PARAMETER SYSTEM
namespace parameter_function {

__STATIC_INLINE int32_t pfun_inl_signed_clamp(int32_t v) {
  return __SSAT(v, 28);
}

__STATIC_INLINE int32_t pfun_inl_unsigned_clamp(int32_t v) {
  return __USAT(v, 27);
}

__STATIC_INLINE int32_t pfun_inl_signed_clamp_fullrange(int32_t v) {
  return __SSAT(v, 28) << 4;
}

__STATIC_INLINE int32_t pfun_inl_unsigned_clamp_fullrange(int32_t v) {
  return __USAT(v, 27) << 4;
}

__STATIC_INLINE int32_t pfun_inl_signed_clamp_squarelaw(int32_t v) {
  int32_t psat = __SSAT(v, 28) << 4;
  if (psat > 0)
    return ___SMMUL(psat, psat) >> 3;
  else
    return -___SMMUL(psat, psat) >> 3;
}

__STATIC_INLINE int32_t pfun_inl_unsigned_clamp_squarelaw(int32_t v) {
  int32_t psat = __USAT(v, 27) << 4;
  return ___SMMUL(psat, psat) >> 3;
}

__STATIC_INLINE int32_t pfun_inl_signed_clamp_fullrange_squarelaw(int32_t v) {
  int32_t psat = __SSAT(v, 28) << 4;
  if (psat > 0)
    return ___SMMUL(psat, psat) << 1;
  else
    return -___SMMUL(psat, psat) << 1;
}

__STATIC_INLINE int32_t pfun_inl_unsigned_clamp_fullrange_squarelaw(int32_t v) {
  int32_t psat = __USAT(v, 27) << 4;
  return ___SMMUL(psat, psat) << 1;
}

__STATIC_INLINE int32_t pfun_inl_kexpltime(int32_t v) {
  int32_t in = (-v);
  int32_t out;
  MTOF(in, out);
  return out >> 2;
}

__STATIC_INLINE int32_t pfun_inl_kexpdtime(int32_t v) {
  int32_t in = (-v);
  int32_t out;
  MTOF(in, out);
  return 0x7FFFFFFF - (out >> 2);
}
#if 1
void pf_signed_clamp(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_signed_clamp(p->d.frac.modvalue);
}

void pf_unsigned_clamp(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_unsigned_clamp(p->d.frac.modvalue);
}

void pf_signed_clamp_fullrange(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_signed_clamp_fullrange(p->d.frac.modvalue);
}

void pf_unsigned_clamp_fullrange(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_unsigned_clamp_fullrange(p->d.frac.modvalue);
}

void pf_signed_clamp_squarelaw(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_signed_clamp_squarelaw(p->d.frac.modvalue);
}

void pf_unsigned_clamp_squarelaw(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_unsigned_clamp_squarelaw(p->d.frac.modvalue);
}

void pf_signed_clamp_fullrange_squarelaw(Parameter_t *p) {
    p->d.frac.finalvalue = pfun_inl_signed_clamp_fullrange_squarelaw(p->d.frac.modvalue);
}

void pf_unsigned_clamp_fullrange_squarelaw(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_unsigned_clamp_fullrange_squarelaw(p->d.frac.modvalue);
}

void pf_kexpltime(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_kexpltime(p->d.frac.modvalue);
}

void pf_kexpdtime(Parameter_t *p) {
  p->d.frac.finalvalue = pfun_inl_kexpdtime(p->d.frac.modvalue);
}
#endif

} // namespace parameter_function


// LEGACY
#if 0
void pfun_signed_clamp(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_signed_clamp(p->modvalue);
}

void pfun_unsigned_clamp(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_unsigned_clamp(p->modvalue);
}

void pfun_signed_clamp_fullrange(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_signed_clamp_fullrange(p->modvalue);
}

void pfun_unsigned_clamp_fullrange(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_unsigned_clamp_fullrange(p->modvalue);
}

void pfun_signed_clamp_squarelaw(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_signed_clamp_squarelaw(p->modvalue);
}

void pfun_unsigned_clamp_squarelaw(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_unsigned_clamp_squarelaw(p->modvalue);
}

void pfun_signed_clamp_fullrange_squarelaw(ParameterExchange_t *p) {
    p->finalvalue = parameter_function::pfun_inl_signed_clamp_fullrange_squarelaw(p->modvalue);
}

void pfun_unsigned_clamp_fullrange_squarelaw(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_unsigned_clamp_fullrange_squarelaw(p->modvalue);
}

void pfun_kexpltime(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_kexpltime(p->modvalue);
}

void pfun_kexpdtime(ParameterExchange_t *p) {
  p->finalvalue = parameter_function::pfun_inl_kexpdtime(p->modvalue);
}
#endif

#endif
