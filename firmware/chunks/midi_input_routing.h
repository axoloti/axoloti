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

// ------ midi in routing table chunk --------------------------------
#ifndef CHUNK_MIDI_INPUT_ROUTING_H
#define CHUNK_MIDI_INPUT_ROUTING_H

#include "fourcc.h"
#include "midi_routing.h"

#define fourcc_midi_input_routing FOURCC('M','I','R','2')

typedef struct {
	chunk_header_t header;
	midi_routing_t * (routing_table[4]);
} chunk_midi_input_routing_t;

#endif
