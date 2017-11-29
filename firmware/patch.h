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
#include "patch_chunk_reader.h"
#include "usbh_patch.h"

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

  uint32_t npresets;
  uint32_t npreset_entries;
  PresetParamChange_t *pPresets; // is a npreset array of npreset_entries of PresetParamChange_t

  uint32_t nparams;
  Parameter_t *params;
  Parameter_name_t *param_names;

  int nobjects;
  ui_object_t *objects;

  uint32_t initpreset_size;
  void *pInitpreset;

  int32_t ndisplayVector_size;
  int32_t *pDisplayVector;
  int32_t ndisplay_metas;
  Display_meta_t *pDisplay_metas;

  uint32_t patchID;
//  void *meta_table;
} patchMeta_t;

extern patchMeta_t patchMeta;

extern int dspLoadPct; // DSP load in percent

typedef enum {
  START_SD = -1,
  START_FLASH = -2,
  BY_FILENAME = -3,
  LIVE = -4,
  UNINITIALIZED = -5
// and positive numbers are index in patch bank
} loadPatchIndex_t;
extern loadPatchIndex_t loadPatchIndex;

typedef enum {
  RUNNING = 0,
  STOPPED = 1,
  STOPPING = 2,
  STARTFAILED = 3,
} patchStatus_t;

extern volatile patchStatus_t patchStatus;

// midi clock
typedef struct {
	int active;
	int32_t period;
	int32_t counter;
	int32_t song_position;
} midi_clock_t;

extern midi_clock_t midi_clock;

void InitPatch0(void);
int StartPatch(void);
void StopPatch(void);

void start_dsp_thread(void);

#define PATCHMAINLOC 0x20000000
// aliased to ICODE by MEMRMP
#define PATCHMAINLOC_ALIASED 0x00000000

// patch is located in sector 11
#define PATCHFLASHLOC 0x080E0000
#define PATCHFLASHSIZE 0xB000

void StartLoadPatchTread(void);
void LoadPatch(const char *name);
void LoadPatchStartSD(void);
void LoadPatchStartFlash(void);
void LoadPatchIndexed(uint32_t index);
loadPatchIndex_t GetIndexOfCurrentPatch(void);
void WritePatch(void);

void codec_clearbuffer(void);

int get_USBH_LL_GetURBState(void);
int get_USBH_LL_SubmitURB(void);
#endif //__PATCH_H
