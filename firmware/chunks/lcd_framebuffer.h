/**
 * Copyright (C) 2017 Johannes Taelman
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

// ------ lcd framebuffer ---------------------------------------------
#ifndef CHUNK_LCD_FRMEBUFFER_H
#define CHUNK_LCD_FRMEBUFFER_H

#include "fourcc.h"

#define fourcc_lcd_framebuffer FOURCC('L','C','D','F')

typedef struct {
	chunk_header_t header;
	int width;
	int height;
	int pixeltype; // = 0
	uint8_t *data;
} chunk_lcd_framebuffer_t;

#endif
