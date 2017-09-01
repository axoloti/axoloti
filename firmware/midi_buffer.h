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
#ifndef MIDI_BUFFER_H

#define MIDI_RING_BUFFER_SIZE 32

#include "ch.h"

/* -------
 * MIDI input buffer
 */

typedef union {
	struct {
		unsigned cin :4;
		unsigned port :4;
		uint8_t b0;
		uint8_t b1;
		uint8_t b2;
	} fields;
	struct {
		uint8_t ph;
		uint8_t b0;
		uint8_t b1;
		uint8_t b2;
	} bytes;
	int32_t word;
} midi_message_t;

typedef struct {
	volatile int32_t read_index;
	volatile int32_t write_index;
	midi_message_t buf[MIDI_RING_BUFFER_SIZE];
} midi_input_buffer_t;

/* put method, non-blocking, discarding data when overflowing */
msg_t midi_input_buffer_put(midi_input_buffer_t *obj, midi_message_t midi);

/* get method, non-blocking */
msg_t midi_input_buffer_get(midi_input_buffer_t *obj, midi_message_t *midi);

void midi_input_buffer_objinit(midi_input_buffer_t *obj);
void midi_intput_buffer_deinit(midi_input_buffer_t *obj);

/* -------
 * MIDI output buffer
 */

typedef void (*midi_output_buffer_notify_t)(void *obj);

typedef struct {
	volatile int32_t read_index;
	volatile int32_t write_index;
	midi_output_buffer_notify_t notify;
	midi_message_t buf[MIDI_RING_BUFFER_SIZE];
} midi_output_buffer_t;

/* put method, non-blocking, discarding data when overflowing */
msg_t midi_output_buffer_put(midi_output_buffer_t *obj, midi_message_t midi);

/* get method, non-blocking */
msg_t midi_output_buffer_get(midi_output_buffer_t *obj, midi_message_t *midi);

int midi_output_buffer_getpending(midi_output_buffer_t *obj);

int midi_output_buffer_get_available(midi_output_buffer_t *obj);

void midi_output_buffer_objinit(midi_output_buffer_t *obj, midi_output_buffer_notify_t notify);

void midi_output_buffer_reset(midi_output_buffer_t *obj);

void midi_output_buffer_deinit(midi_output_buffer_t *obj);

void midi_output_buffer_notify(midi_output_buffer_t *obj);

#define MIDI_BUFFER_H
#endif
