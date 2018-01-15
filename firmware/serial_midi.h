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
#ifndef __SERIAL_MIDI_H
#define __SERIAL_MIDI_H

#include <stdint.h>
#include <midi.h>

void serial_midi_init(void);

extern midi_output_buffer_t midi_output_din;

// report the number of bytes pending for transmission
int  serial_MidiGetOutputBufferPending(void);

extern midi_routing_t midi_inputmap_din;
extern midi_routing_t midi_outputmap_din;

// obsolete
void serial_MidiSend1(uint8_t b0);
void serial_MidiSend2(uint8_t b0, uint8_t b1);
void serial_MidiSend3(uint8_t b0, uint8_t b1, uint8_t b2);

#endif
