/*
 * midi_decoder.h
 *
 * Decodes a serial MIDI stream into MIDI Event Packets
 * (cfr. https://usb.org/sites/default/files/midi10.pdf )
 *
 *      Author: jtaelman
 */

#ifndef MIDI_DECODER_H_
#define MIDI_DECODER_H_

#include "midi.h"
#include "stdint.h"

typedef void (* midi_rcv_cb_t)(midi_message_t);

typedef struct {
	midi_rcv_cb_t midi_rcv_cb;
	unsigned char midiByte0;
	unsigned char midiByte1;
	unsigned char midiByte2;
	unsigned char midiCurData;
	unsigned char midiNumData;
	unsigned char inSysEx;
} midi_decoder_state_t;

extern void midi_decoder_process(midi_decoder_state_t *state, uint8_t data);

#endif /* MIDI_DECODER_H_ */
