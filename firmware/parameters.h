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
#ifndef __PARAMETERS_H
#define __PARAMETERS_H
#include <stdint.h>

typedef struct {
  int32_t header;
  uint32_t patchID;
  int32_t value;
  int32_t index;
} PExMessage;

//typedef int32_t (*ParameterExchangeFn_t)(int32_t, int32_t);
// param 1: modvalue
// param 2: parameterIndex
// return value: final value

//typedef ParameterExchange_t;

//typedef void (*ParameterExchangeFn_t)(struct ParameterExchange_t *);

typedef struct _ParameterExchange {
  int32_t value;      // parameter value
  int32_t modvalue;   // parameter value after modulation
  int32_t finalvalue; // parameter value after modulation and function evaluation
  // in certain cases ( PropagateToVoices ) finalvalue is used as additional input data for pFunction
  //ParameterExchangeFn_t pfunction; // function to call after modulation
  void (*pfunction)(struct _ParameterExchange *);
  uint32_t signals; // flags to distribute value changes to different targets (usb, midi...)
  // signals are bitmasks, indicating which subsystems need to be signaled of this change:
  // origins :
  //      masks : 0x00000001 -> USB parameter exchange
  //              0x00000002 -> DIN MIDI port
  //              0x00000004 -> Display
  //              0x00000008 -> buttons and dials
  //              0x00000010 -> polling readback enabled (never clear this) (OBSOLETE)
  // so a parameter received on USB will set the mask to 0xFFFFFFFE
  // signaling the other subsystems, but preventing echoing the change again via USB
} ParameterExchange_t;

typedef struct {
  int32_t parameterIndex;
  int32_t amount;
} PExModulationTarget_t;


void PExModulationSourceChange(PExModulationTarget_t *modulation,
                               int32_t nTargets,
                               ParameterExchange_t *parameters,
                               int32_t *oldvalue,
                               int32_t value);

void PExParameterChange(ParameterExchange_t *param, int32_t value,
                        uint32_t signals);

void ApplyPreset(unsigned int index);

#endif
