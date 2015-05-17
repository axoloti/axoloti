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
#ifndef __SERIAL_MIDI_H
#define __SERIAL_MIDI_H

#include <stdint.h>

void serial_midi_init(void);
void serial_MidiSend1(uint8_t b0);
void serial_MidiSend2(uint8_t b0, uint8_t b1);
void serial_MidiSend3(uint8_t b0, uint8_t b1, uint8_t b2);

int  serial_MidiGetOutputBufferPending(void);

#endif
