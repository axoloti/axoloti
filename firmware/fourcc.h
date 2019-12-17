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
#ifndef FOURCC_H
#define FOURCC_H

#include <stdint.h>

typedef uint32_t fourcc_t;

#define FOURCC(a,b,c,d) ( (fourcc_t) ((((uint8_t)d)<<24) | (((uint8_t)c)<<16) | (((uint8_t)b)<<8) | ((uint8_t)a)) )

typedef struct {
	uint32_t fourcc;
	uint32_t size;
	// uint8_t data[size];
} chunk_header_t;

#define CHUNK_HEADER(type) \
{.fourcc = fourcc_##type, .size= sizeof(chunk_##type##_t) - sizeof(chunk_header_t)}

#endif //FOURCC_H
