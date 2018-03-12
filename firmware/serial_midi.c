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

#include "ch.h"
#include "hal.h"
#include "axoloti_board.h"
#include "midi.h"
#include "midi_routing.h"
#include "serial_midi.h"
#include "patch.h"

const unsigned char StatusLengthLookup[16] = {0, 0, 0, 0, 0, 0, 0, 0, 3, // 0x80=note off, 3 bytes
                                               3, // 0x90=note on, 3 bytes
                                               3, // 0xa0=poly pressure, 3 bytes
                                               3, // 0xb0=control change, 3 bytes
                                               2, // 0xc0=program change, 2 bytes
                                               2, // 0xd0=channel pressure, 2 bytes
                                               3, // 0xe0=pitch bend, 3 bytes
                                               -1 // 0xf0=other things. may vary.
    };

const signed char SysMsgLengthLookup[16] = {-1, // 0xf0=sysex start. may vary
    2, // 0xf1=MIDI Time Code. 2 bytes
    3, // 0xf2=MIDI Song position. 3 bytes
    2, // 0xf3=MIDI Song Select. 2 bytes.
    1, // 0xf4=undefined
    1, // 0xf5=undefined
    1, // 0xf6=TUNE Request
    1, // 0xf7=sysex end.
    1, // 0xf8=timing clock. 1 byte
    1, // 0xf9=proposed measure end?
    1, // 0xfa=start. 1 byte
    1, // 0xfb=continue. 1 byte
    1, // 0xfc=stop. 1 byte
    1, // 0xfd=undefined
    1, // 0xfe=active sensing. 1 byte
    3 // 0xff= not reset, but a META-EVENT, which is always 3 bytes
    };

midi_routing_t midi_inputmap_din = {
		.name = "DIN",
		.nports = 1,
		.bmvports = {
				0b0000000000000001
		}
};

midi_routing_t midi_outputmap_din = {
			.name = "DIN",
			.nports = 1,
			.bmvports = {
					0b0000000000000001
			}
};

static unsigned char MidiByte0;
static unsigned char MidiByte1;
static unsigned char MidiByte2;
static unsigned char MidiCurData;
static unsigned char MidiNumData;

// CIN for everyting except sysex
inline uint8_t SMidi_calcCIN(uint8_t b0) {
    return (b0 & 0xF0 ) >> 4;
}

__STATIC_INLINE void dispatch_midi_input(midi_message_t m) {
	  int portmap = midi_inputmap_din.bmvports[0];
	  int v;
	  for (v=0;v<16;v++) {
		  if (portmap & 1) {
			  m.fields.port = v;
			  midi_input_buffer_put(&midi_input_buffer, m);
		  }
		  portmap = portmap>>1;
	  }
}

static void serial_MidiInByteHandler(uint8_t data) {
  int8_t len;
  if (data & 0x80) {
    len = StatusLengthLookup[data >> 4];
    if (len == -1) {
      len = SysMsgLengthLookup[data - 0xF0];
      if (len == 1) {
    	  // TODO: sysex handling...
    	  midi_message_t m;
    	  m.bytes.ph = SMidi_calcCIN(data);
    	  m.bytes.b0 = data;
    	  m.bytes.b1 = 0;
    	  m.bytes.b2 = 0;
    	  dispatch_midi_input(m);
      }
      else {
        MidiByte0 = data;
        MidiNumData = len - 1;
        MidiCurData = 0;
      }
    }
    else {
      MidiByte0 = data;
      MidiNumData = len - 1;
      MidiCurData = 0;
    }
  }
  else // not a status byte
  {
    if (MidiCurData == 0) {
      MidiByte1 = data;
      if (MidiNumData == 1) {
        // 2 byte message complete
    	  midi_message_t m;
    	  m.bytes.ph = SMidi_calcCIN(MidiByte0);
    	  m.bytes.b0 = MidiByte0;
    	  m.bytes.b1 = MidiByte1;
    	  m.bytes.b2 = 0;
    	  dispatch_midi_input(m);
        MidiCurData = 0;
      }
      else
        MidiCurData++;
    }
    else if (MidiCurData == 1) {
      MidiByte2 = data;
      if (MidiNumData == 2) {
    	  midi_message_t m;
    	  m.bytes.ph = SMidi_calcCIN(MidiByte0);
    	  m.bytes.b0 = MidiByte0;
    	  m.bytes.b1 = MidiByte1;
    	  m.bytes.b2 = MidiByte2;
    	  dispatch_midi_input(m);
        MidiCurData = 0;
      }
    }
  }
}

// Midi OUT

void serial_MidiSend(midi_message_t midimsg) {
	// TODO: running status
	// TODO: skip other messages when sysex is in progress
	switch(midimsg.fields.cin){
  case 0x4: // sysex start/continue
    sdWrite(&SDMIDI, &midimsg.bytes.b0, 3);
    break;
  case 0x5: // end 1 byte
    sdWrite(&SDMIDI, &midimsg.bytes.b0, 1);
    break;
  case 0x6: // end 2 byte
    sdWrite(&SDMIDI, &midimsg.bytes.b0, 2);
    break;
  case 0x7: // end 3 byte
    sdWrite(&SDMIDI, &midimsg.bytes.b0, 3);
    break;

	case 0x8: // note-off
	case 0x9: // note-on
	case 0xA: // PolyKeyPress
	case 0xB: // Control change
		sdWrite(&SDMIDI, &midimsg.bytes.b0, 3);
		break;
	case 0xC: // program change
	case 0xD: // channel pressure
		sdWrite(&SDMIDI, &midimsg.bytes.b0, 2);
		break;
	case 0xE: // pitch bend
		sdWrite(&SDMIDI, &midimsg.bytes.b0, 3);
		break;
	case 0xF: // single byte
		sdWrite(&SDMIDI, &midimsg.bytes.b0, 1);
		break;
	}
}


void serial_MidiSend1(uint8_t b0) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.fields.cin = b0>>4;
	serial_MidiSend(m);
}

void serial_MidiSend2(uint8_t b0, uint8_t b1) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.fields.cin = b0>>4;
	serial_MidiSend(m);
}

void serial_MidiSend3(uint8_t b0, uint8_t b1, uint8_t b2) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = b2;
	m.fields.cin = b0>>4;
	serial_MidiSend(m);
}

int serial_MidiGetOutputBufferPending(void) {
// todo: check references!
  return 0;//chOQGetFullI(&SDMIDI.oqueue);
}

// Midi UART...
static const SerialConfig sdMidiCfg = {31250, // baud
    0, 0, 0};

static THD_WORKING_AREA(waThreadMidiIn, 256);
static THD_FUNCTION(ThreadMidiIn, arg) {
  (void)arg;
  chRegSetThreadName("midi_din_in");
  while (1) {
    char ch;
    ch = sdGet(&SDMIDI);
    serial_MidiInByteHandler(ch);
  }
}

midi_output_buffer_t midi_output_din;

static THD_WORKING_AREA(waThreadMidiOut, 256);
static THD_FUNCTION(ThreadMidiOut, arg) {
	(void) arg;
	chRegSetThreadName("midi_din_out");
	while (1) {
		eventmask_t evt = chEvtWaitOne(1);
		(void) evt;
		midi_message_t m;
		msg_t r;
		r = midi_output_buffer_get(&midi_output_din, &m);
		while (r == MSG_OK ) {
			serial_MidiSend(m);
			r = midi_output_buffer_get(&midi_output_din, &m);
		}
	}
}

static thread_t * thd_midi_din_writer;

static void notify(void *obj) {
  chEvtSignal(thd_midi_din_writer,1);
}

void serial_midi_init(void) {
  /*
   * Activates the serial driver 2 using the driver default configuration.
   * PA2(TX) and PA3(RX) are routed to USART2.
   */

  load_midi_routing(&midi_inputmap_din, in);
  load_midi_routing(&midi_outputmap_din, out);

  // RX
  palSetPadMode(GPIOG, 9, PAL_MODE_ALTERNATE(8) | PAL_MODE_INPUT_PULLUP);
  // TX
  palSetPadMode(GPIOG, 14, PAL_MODE_ALTERNATE(8) | PAL_STM32_OTYPE_OPENDRAIN);

  sdStart(&SDMIDI, &sdMidiCfg);
  chThdCreateStatic(waThreadMidiIn, sizeof(waThreadMidiIn), NORMALPRIO, ThreadMidiIn,
                    NULL);
  thd_midi_din_writer = chThdCreateStatic(waThreadMidiOut, sizeof(waThreadMidiOut), NORMALPRIO, ThreadMidiOut,
                    NULL);
  midi_output_buffer_objinit(&midi_output_din, notify);
}
