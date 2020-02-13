/*
 * midi_encoder.c
 *
 *      Author: jtaelman
 */

#include "midi_encoder.h"

static const char cin_msg_length[16] = {
	0, // 0x0: Reserved for future extensions
	0, // 0x1: Reserved for future extensions
	2, // 0x2: Two-byte System Common messages
	3, // 0x3: Three-byte System Common messages
	3, // 0x4: sysex start/continue
	1, // 0x5: end 1 byte
	2, // 0x6: end 2 byte
	3, // 0x7: end 3 byte
	3, // 0x8: note-off
	3, // 0x9: note-on
	3, // 0xA: PolyKeyPress
	3, // 0xB: Control change
	2, // 0xC: program change
	2, // 0xD: channel pressure
	3, // 0xE: pitch bend
	1  // 0x0F: single byte
};

int midi_encoder_get_length(midi_message_t midi_msg) {
	return cin_msg_length[midi_msg.fields.cin];
}
