/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
#include "parameters.h"
#include "patch.h"
#include "axoloti_math.h"

#if 1 // Obsolete parameter structure
void PExModulationSourceChange(PExModulationTarget_t *modulation,
                               int32_t nTargets,
                               ParameterExchange_t *parameters,
                               int32_t *oldvalue,
                               int32_t value) {
  PExModulationTarget_t *s = modulation;
  int t;
  for (t = 0; t < nTargets; t++) {
    PExModulationTarget_t *target = &s[t];
    if (target->parameterIndex == -1)
      continue;
    ParameterExchange_t *PEx = &parameters[target->parameterIndex];
    int32_t v = PEx->modvalue;
    v -= ___SMMUL(*oldvalue, target->amount) << 5;
    v += ___SMMUL(value, target->amount) << 5;
    PEx->modvalue = v;
    if (PEx->pfunction) {
      (PEx->pfunction)(PEx);
      // TBC: modulation on root of polyphonic-subpatch-parameters
    }
    else {
      PEx->finalvalue = v;
    }
  }
  *oldvalue = value;
}

void PExParameterChange(ParameterExchange_t *param, int32_t value,
                        uint32_t signals) {
  param->modvalue -= param->value;
  param->value = value;
  param->modvalue += param->value;
  param->signals |= signals;
  if (param->pfunction) {
    (param->pfunction)(param);
  }
  else {
    param->finalvalue = param->modvalue;
  }
}
#endif


#if NEW_PARAMETER_SYSTEM

void ParameterChange(Parameter_t *param, int32_t value, uint32_t signals) {
switch (param->type) {
case param_type_frac_sq27:
case param_type_frac_uq27:
	  param->d.frac.modvalue -= param->d.frac.value;
	  param->d.frac.value = value;
	  param->d.frac.modvalue += param->d.frac.value;
	  break;
case param_type_int: {
	if (value<param->d.intt.minimum) value = param->d.intt.minimum;
	if (value>param->d.intt.maximum) value = param->d.intt.maximum;
	param->d.intt.value = value;
	param->d.intt.modvalue = value;
} break;
default:
// we don't support modulations on other types for now
	param->d.intt.value = value;
	param->d.intt.modvalue = value;
}

  param->signals |= signals;
  if (param->pfunction)
    (param->pfunction)(param);
  else
	  // assuming finalvalue and modvalue fields are on the same position for integer and bit parameters:
    param->d.frac.finalvalue = param->d.frac.modvalue;
}

#endif

void ApplyPreset(unsigned int index) {
  if (patchMeta.fptr_applyPreset != 0)
    (patchMeta.fptr_applyPreset)(index);
}
