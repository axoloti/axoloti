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
#ifndef __SDCARD_H
#define __SDCARD_H

#include "ch.h"

void sdcard_init(void);
int sdcard_loadPatch1(char *fname);
void sdcard_attemptMountIfUnmounted(void);
void sdcard_unmount(void);

void cmd_sdiotest(BaseSequentialStream *chp, int argc, char *argv[]);
void cmd_tree(BaseSequentialStream *chp, int argc, char *argv[]);

//extern FATFS SDC_FS;
extern bool_t fs_ready;

extern uint32_t fbuff[256];

#endif
