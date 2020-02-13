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
#ifndef MIDI_DIN_H
#define MIDI_DIN_H

#include <stdint.h>
#include "midi.h"
#include "midi_buffer.h"

void midi_din_init(void);

extern midi_output_buffer_t midi_din_output;

// report the number of bytes pending for transmission
int midi_din_GetOutputBufferPending(void);

extern midi_routing_t midi_din_inputmap;
extern midi_routing_t midi_din_outputmap;

#endif
