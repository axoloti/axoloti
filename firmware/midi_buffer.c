#include "ch.h"
#include "hal.h"
#include "midi_buffer.h"
#include "exceptions.h"

/* -------
 * MIDI input buffer
 */

void midi_input_buffer_objinit(midi_input_buffer_t *obj) {
	  // make no bytes available for output, initialise will reset
	  obj->read_index  = 0;
	  obj->write_index = MIDI_RING_BUFFER_SIZE - 1;
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
