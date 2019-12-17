/**
 * Copyright (C) 2018 Johannes Taelman
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

// ------ gpio ADC table chunk ----------------------------------------
#ifndef PATCH_NAME_H
#define PATCH_NAME_H

#include "fourcc.h"

#define fourcc_patch_name FOURCC('P','C','H','N')

typedef struct {
	chunk_header_t header;
	char *patch_name;
} chunk_patch_name_t;

#endif
