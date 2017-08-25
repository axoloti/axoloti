#ifndef CHUNK_MIDI_BUFFER_H
#define CHUNK_MIDI_BUFFER_H

#include "fourcc.h"
#include "../midi_buffer.h"

#define fourcc_midi_buffer FOURCC('M','I','B','1')

typedef struct {
	chunk_header_t header;
	midi_input_buffer_t *data;
} chunk_midi_buffer_t;

#endif
