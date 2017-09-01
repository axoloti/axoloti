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

#include "ch.h"
#include "midi_buffer.h"
#include "exceptions.h"

/* -------
 * MIDI input buffer
 */

void midi_input_buffer_objinit(midi_input_buffer_t *obj) {
	  // make no bytes available for output, initialise will reset
	  obj->read_index  = 0;
	  obj->write_index = 0;
}

void midi_intput_buffer_deinit(midi_input_buffer_t *obj) {
	obj->read_index = 0;
	obj->write_index = -1;
}

msg_t midi_input_buffer_put(midi_input_buffer_t *obj, midi_message_t midi) {
	// FIXME: make reentrant
	if (obj->write_index + 1 == 0) return -1;
	int32_t next = (obj->write_index + 1) % MIDI_RING_BUFFER_SIZE;
	if (next == obj->read_index) {
		report_usbh_midi_ringbuffer_overflow();
		return MSG_TIMEOUT;
	}
	obj->buf[next] = midi;
	obj->write_index = next;
	return MSG_OK;
}

msg_t midi_input_buffer_get(midi_input_buffer_t *obj, midi_message_t *midi) {
    if (obj->read_index != obj->write_index) {
    	obj->read_index = (obj->read_index + 1) % MIDI_RING_BUFFER_SIZE;
    	*midi = obj->buf[obj->read_index];
    	return MSG_OK;
    } else return MSG_TIMEOUT;
}

/* -------
 * MIDI output buffer
 */

void midi_output_buffer_objinit(midi_output_buffer_t *obj, midi_output_buffer_notify_t notify) {
	obj->notify = notify;
	obj->read_index = 0;
	obj->write_index = 0;
}

void midi_output_buffer_reset(midi_output_buffer_t *obj) {
    obj->read_index = obj->write_index = 0;
}

void midi_output_buffer_deinit(midi_output_buffer_t *obj) {
	obj->read_index = 0;
	obj->write_index = -1;
}

msg_t midi_output_buffer_put(midi_output_buffer_t *obj, midi_message_t midi) {
	// TODO: make reentrant
	if (obj->write_index + 1 == 0) return -1;
	int32_t next = (obj->write_index + 1) % MIDI_RING_BUFFER_SIZE;
	if (next == obj->read_index) {
		report_usbh_midi_ringbuffer_overflow();
		return MSG_TIMEOUT;
	}
	obj->buf[next] = midi;
	obj->write_index = next;
	obj->notify(obj);
	return MSG_OK;
}

msg_t midi_output_buffer_get(midi_output_buffer_t *obj, midi_message_t *midi) {
    if (obj->read_index != obj->write_index) {
    	obj->read_index = (obj->read_index + 1) % MIDI_RING_BUFFER_SIZE;
    	*midi = obj->buf[obj->read_index];
    	return MSG_OK;
    } else return MSG_TIMEOUT;
}

void midi_output_buffer_notify(midi_output_buffer_t *obj) {
	obj->notify(obj);
}

int midi_output_buffer_getpending(midi_output_buffer_t *obj) {
    if (obj->write_index >= obj->read_index) {
        return obj->write_index - obj->read_index;
    }
    return obj->write_index + MIDI_RING_BUFFER_SIZE - obj->read_index;
}

int midi_output_buffer_get_available(midi_output_buffer_t *obj) {
	return MIDI_RING_BUFFER_SIZE - midi_output_buffer_getpending(obj) - 1;
}
