/*
 * midi_decoder.c
 *
 *      Author: jtaelman
 */

#include "ch.h"
#include "midi_decoder.h"


static const unsigned char StatusLengthLookup[16] = {0, 0, 0, 0, 0, 0, 0, 0, 3, // 0x80=note off, 3 bytes
                                               3, // 0x90=note on, 3 bytes
                                               3, // 0xa0=poly pressure, 3 bytes
                                               3, // 0xb0=control change, 3 bytes
                                               2, // 0xc0=program change, 2 bytes
                                               2, // 0xd0=channel pressure, 2 bytes
                                               3, // 0xe0=pitch bend, 3 bytes
                                               -1 // 0xf0=other things. may vary.
    };

static const signed char SysMsgLengthLookup[16] = {-1, // 0xf0=sysex start. may vary
    2, // 0xf1=MIDI Time Code. 2 bytes
    3, // 0xf2=MIDI Song position. 3 bytes
    2, // 0xf3=MIDI Song Select. 2 bytes.
    1, // 0xf4=undefined
    1, // 0xf5=undefined
    1, // 0xf6=TUNE Request
    -1, // 0xf7=sysex end.
    1, // 0xf8=timing clock. 1 byte
    1, // 0xf9=proposed measure end?
    1, // 0xfa=start. 1 byte
    1, // 0xfb=continue. 1 byte
    1, // 0xfc=stop. 1 byte
    1, // 0xfd=undefined
    1, // 0xfe=active sensing. 1 byte
    3 // 0xff= not reset, but a META-EVENT, which is always 3 bytes
    };


// CIN for everyting except sysex
static inline uint8_t SMidi_calcCIN(uint8_t b0) {
    return (b0 & 0xF0 ) >> 4;
}


void midi_decoder_process(midi_decoder_state_t *state, uint8_t data) {
  int8_t len;
  if (data & 0x80) {
    len = StatusLengthLookup[data >> 4];
    if (len == -1) {
      len = SysMsgLengthLookup[data - 0xF0];
      if (len == 1) {
    	  midi_message_t m;
    	  m.bytes.ph = SMidi_calcCIN(data);
    	  m.bytes.b0 = data;
    	  m.bytes.b1 = 0;
    	  m.bytes.b2 = 0;
    	  state->midi_rcv_cb(m);
      } else if (data == MIDI_SYSEX_START) {
    	  state->inSysEx = 1;
    	  state->midiNumData = 0;
    	  state->midiByte1 = data;
    	  state->midiCurData = 1;
      } else if (data == MIDI_SYSEX_END) {
    	  state->inSysEx = 0;
        switch (state->midiCurData) {
          case 0: {
            midi_message_t m;
            m.bytes.ph = 0x5; // SysEx ends with following single byte.
            m.bytes.b0 = MIDI_SYSEX_END;
            m.bytes.b1 = 0;
            m.bytes.b2 = 0;
            state->midi_rcv_cb(m);
          } break;
          case 1: {
            midi_message_t m;
            m.bytes.ph = 0x6; // SysEx ends with following two bytes.
            m.bytes.b0 = state->midiByte1;
            m.bytes.b1 = MIDI_SYSEX_END;
            m.bytes.b2 = 0;
            state->midi_rcv_cb(m);
          } break;
          case 2: {
            midi_message_t m;
            m.bytes.ph = 0x7; // SysEx ends with following three bytes.
            m.bytes.b0 = state->midiByte1;
            m.bytes.b1 = state->midiByte2;
            m.bytes.b2 = MIDI_SYSEX_END;
            state->midi_rcv_cb(m);
          } break;
          default:
            chSysHalt("MIDI SysEx invalid state");
        }
      } else {
    	state->midiByte0 = data;
    	state->midiNumData = len - 1;
    	state->midiCurData = 0;
      }
    } else {
      state->midiByte0 = data;
      state->midiNumData = len - 1;
      state->midiCurData = 0;
    }
  } else { // not a status byte
    if (state->midiCurData == 0) {
      state->midiByte1 = data;
      if (state->midiNumData == 1) {
        // 2 byte message complete
        midi_message_t m;
        m.bytes.ph = SMidi_calcCIN(state->midiByte0);
        m.bytes.b0 = state->midiByte0;
        m.bytes.b1 = state->midiByte1;
        m.bytes.b2 = 0;
        state->midi_rcv_cb(m);
        state->midiCurData = 0;
      } else {
    	  state->midiCurData++;
      }
    } else if (state->midiCurData == 1) {
      state->midiByte2 = data;
      if (state->midiNumData == 2) {
    	  midi_message_t m;
    	  m.bytes.ph = SMidi_calcCIN(state->midiByte0);
    	  m.bytes.b0 = state->midiByte0;
    	  m.bytes.b1 = state->midiByte1;
    	  m.bytes.b2 = state->midiByte2;
    	  state->midi_rcv_cb(m);
    	  state->midiCurData = 0;
      } else {
    	  state->midiCurData++;
      }
    } else if (state->midiCurData == 2) {
      midi_message_t m;
      m.bytes.ph = 0x04; // SysEx starts or continues
      m.bytes.b0 = state->midiByte1;
      m.bytes.b1 = state->midiByte2;
      m.bytes.b2 = data;
      state->midi_rcv_cb(m);
      state->midiCurData = 0;
    }
  }
}

