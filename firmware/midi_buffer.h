#ifndef MIDI_BUFFER_H

#define MIDI_RING_BUFFER_SIZE 32

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
	size_t size;
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


/* -------
 * MIDI input port mapping
 *
 * all patch midi input and output to use 16 virtual input and output ports
 *
 * map each real input port to up to 4 "virtual" input port
 * map each "virtual" output port to up to 4 "virtual" output ports
 *
 * one fixed virtual input port # specifically for clock
 * one fixed virtual input port # specifically for regular keybd playing
 * a virtual port can serve for specific midi controllers,
 *   for example a launchpad, and be handled specifically without
 *   injecting note-events in a synth patch
 *
 * perhaps a few dedicated virtual ports ID's could serve for midi-thru routing
 *
 * how to trap midi/break feedback loops?
 */
#define MIDI_INPUT_REMAP_ENTRIES 4

typedef int8_t midi_input_remap_t[MIDI_INPUT_REMAP_ENTRIES];

#define MIDI_BUFFER_H
#endif
