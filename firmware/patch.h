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
#ifndef __PATCH_H
#define __PATCH_H
#include <stdint.h>
#include "ch.h"
#include "hal.h"
#include "ui.h"
#include "axoloti_board.h"
#include "ff.h"
#include "midi.h"
#include "crc32.h"
#include "exceptions.h"

typedef void (*fptr_patch_init_t)(int32_t fwID);
typedef void (*fptr_patch_dispose_t)(void);
typedef void (*fptr_patch_dsp_process_t)(int32_t *, int32_t *);
typedef void (*fptr_patch_midi_in_handler_t)(midi_device_t dev, uint8_t port, uint8_t, uint8_t, uint8_t);
typedef void (*fptr_patch_applyPreset_t)(int32_t);

typedef struct {
  int32_t pexIndex;
  int32_t value;
} PresetParamChange_t;

typedef struct {
  fptr_patch_init_t fptr_patch_init;
  fptr_patch_dispose_t fptr_patch_dispose;
  fptr_patch_dsp_process_t fptr_dsp_process;
  fptr_patch_midi_in_handler_t fptr_MidiInHandler;
  fptr_patch_applyPreset_t fptr_applyPreset;
  uint32_t numPEx;
  ParameterExchange_t *pPExch;
  int32_t *pDisplayVector;
  uint32_t patchID;
  uint32_t initpreset_size;
  void *pInitpreset;
  uint32_t npresets;
  uint32_t npreset_entries;
  PresetParamChange_t *pPresets; // is a npreset array of npreset_entries of PresetParamChange_t
} patchMeta_t;

extern patchMeta_t patchMeta;

extern int dspLoadPct; // DSP load in percent

extern volatile int patchStatus;
// 0-> running
// 1-> stopped
// >1-> stopping

extern int8_t hid_buttons[8];
extern int8_t hid_mouse_x;
extern int8_t hid_mouse_y;

void InitPatch0(void);
void StartPatch(void);
void StopPatch(void);

void start_dsp_thread(void);

#define PATCHMAINLOC 0x20011000

// patch is located in sector 11
#define PATCHFLASHLOC 0x080E0000
#define PATCHFLASHSIZE 0xB000

void StartLoadPatchTread(void);
void LoadPatch(const char *name);
void LoadPatchIndexed(uint32_t index);

#endif //__PATCH_H
