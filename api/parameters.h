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
#ifndef API_PARAMETERS_H
#define API_PARAMETERS_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>

typedef struct {
  int32_t parameterIndex;
  int32_t amount;
} PExModulationTarget_t;

#if 0
// OBSOLETE but kept for backwards compatibility
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


void PExModulationSourceChange(PExModulationTarget_t *modulation,
                               int32_t nTargets,
                               ParameterExchange_t *parameters,
                               int32_t *oldvalue,
                               int32_t value);

void PExParameterChange(ParameterExchange_t *param, int32_t value,
                        uint32_t signals);
#endif

#define PExParameterChange parameter_setVal

#if 1
#define MAX_PARAMETER_NAME_LENGTH 8

enum {
	param_type_undefined = 0,
	param_type_frac_uq27 = 1,
	param_type_frac_sq27 = 2,
	param_type_int = 3,
	param_type_bin_1bit_momentary = 4,
	param_type_bin_1bit_toggle = 5,
	param_type_bin_16bits = 6,
	param_type_bin_32bits = 7
};

enum {
	param_unit_abstract = 0,
	param_unit_time = 1,
	param_unit_freq = 2,
	param_unit_pitch = 3
};

typedef struct {
  int32_t finalvalue; // parameter value after modulation and function evaluation
// would int16_t be sufficient for value, modvalue, offset and multiplier?
  int32_t value;      // parameter dial position
  int32_t modvalue;   // parameter dial position after modulation
  int32_t offset;     // minimum parameter dial position
  int32_t multiplier; // maximum parameter dial position is minimum + multiplier
  // we also need dial position detent info
} ParameterFrac_t;


typedef struct {
  int32_t finalvalue; // parameter value after modulation and function evaluation
  int32_t value;      // parameter dial position
  int32_t modvalue;   // parameter dial position after modulation
  int32_t minimum;    // minimum value after modulation
  int32_t maximum;    // maximum value after modulation
  // no need for detent info
} ParameterInt_t;

typedef struct {
  int32_t finalvalue; // parameter value after modulation and function evaluation
  int32_t value;      // parameter dial position
  int32_t modvalue;   // parameter dial position after modulation
  int32_t nbits;      // number of bits
} ParameterBin_t;


typedef struct _Parameter {
	  uint8_t type; // see enum param_type_*
	  uint8_t unit; // see enum param_unit_*
	  uint16_t signals; // flags to distribute value changes to different targets (usb, midi...)
	  // signals are bitmasks, indicating which subsystems need to be signaled of this change:
	  // origins :
	  //      masks : 0b00000001 -> USB parameter exchange
	  //              0x00000010 -> DIN MIDI port
	  //              0x00000100 -> Display
	  //              0x00001000 -> buttons and dials
	  //              0x00010000 -> polling readback enabled (never clear this) (OBSOLETE)
	  // so a parameter received on USB will set the mask to 0xFFFFFFFE
	  // signaling the other subsystems, but preventing echoing the change again via USB

	  void (*pfunction)(struct _Parameter *);
	  // pfunction allows to project modulated dial position to dsp-algorithm-native values outside k-rate
	  // in certain cases ( PropagateToVoices ) finalvalue is used as additional input data for pFunction
	  //ParameterExchangeFn_t pfunction; // function to call after modulation
	  // hmm this one is hard to manipulate embedded, move to separate table?

	  union {
		  ParameterFrac_t frac;
		  ParameterInt_t intt;
		  ParameterBin_t bin;
	  } d; // can't use static initializers in g++ on a anonymous union, pity...
} Parameter_t;

// separate array of parameter names
// this avoids duplicates for polyphonic sub-patches
typedef struct {
	  char name[MAX_PARAMETER_NAME_LENGTH];
	  // parameter name, not null terminated
	  // using 8 characters avoids memory allocation for embedded modification
	  // and just twice the space of a pointer to somewhere else
	  // also, a "string compare" boils down to 2 int32 compares rather
	  // than a loop...
} Parameter_name_t;

void parameter_setVal(Parameter_t *param, int32_t value, uint32_t signals);

enum {
	display_meta_type_sq27,
	display_meta_type_uq27,
	display_meta_type_chart_sq27,
	display_meta_type_chart_uq27,
	display_meta_type_dial_sq27,
	display_meta_type_dial_uq27,
	display_meta_type_int32,
	display_meta_type_ibar16,
	display_meta_type_ibar32,
	display_meta_type_bool1,
	display_meta_type_bool16,
	display_meta_type_bool32,
	display_meta_type_hex32,
	display_meta_type_frac8s128,
	display_meta_type_frac8u128,
	display_meta_type_vu,
	display_meta_type_undefined
};

typedef struct {
	int32_t display_type;
	char name[MAX_PARAMETER_NAME_LENGTH];
	int32_t *displaydata;
} Display_meta_t;

typedef struct ui_object {
	char name[MAX_PARAMETER_NAME_LENGTH];
	int32_t nparams;
	Parameter_t *params;
	Parameter_name_t *param_names;
	int32_t ndisplays;
	Display_meta_t *displays;
	int32_t nobjects;
	struct ui_object *objects;
} ui_object_t;


#define NEW_PARAMETER_SYSTEM 1

#endif

#ifdef __cplusplus
} // extern "C"
#endif

#endif // API_PARAMETERS_H
