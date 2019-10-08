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

typedef struct {
  int32_t pexIndex;
  int32_t value;
} PresetParamChange_t;

typedef enum {
  RUNNING = 0,
  STOPPED = 1,
  STOPPING = 2,
  STARTFAILED = 3,
} patchStatus_t;

typedef struct patch patch_t;

typedef enum {
  patch_callback_stop = 1
} patch_callback_type;

typedef void (*patch_callback_t)(patch_t *, patch_callback_type);

patch_t * patch_load(const char *name, patch_callback_t patch_callback);
patch_t * patch_loadIndex(int index, patch_callback_t patch_callback);
patch_t * patch_loadStartSD(patch_callback_t patch_callback);

void patch_stop(patch_t * patch);

const char * patch_getError();

void codec_clearbuffer(void); //TODO: cleanup, move out of this header

#endif //__PATCH_H
