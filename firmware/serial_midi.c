/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
#include "serial_midi.h"
#include "patch.h"

static unsigned char StatusLengthLookup[16] = {0, 0, 0, 0, 0, 0, 0, 0, 3, // 0x80=note off, 3 bytes
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

unsigned char MidiByte0;
unsigned char MidiByte1;
unsigned char MidiByte2;
unsigned char MidiCurData;
unsigned char MidiNumData;
unsigned char MidiInChannel;

void serial_MidiInByteHandler(uint8_t data);


void serial_MidiInByteHandler(uint8_t data) {
  int8_t len;
  if (data & 0x80) {
    len = StatusLengthLookup[data >> 4];
    if (len == -1) {
      len = SysMsgLengthLookup[data - 0xF0];
      if (len == 1) {
        MidiInMsgHandler(MIDI_DEVICE_DIN, 1, data, 0, 0);
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
        MidiInMsgHandler(MIDI_DEVICE_DIN, 1, MidiByte0, MidiByte1, 0);
        MidiCurData = 0;
      }
      else
        MidiCurData++;
    }
    else if (MidiCurData == 1) {
      MidiByte2 = data;
      if (MidiNumData == 2) {
        MidiInMsgHandler(MIDI_DEVICE_DIN, 1, MidiByte0, MidiByte1, MidiByte2);
        MidiCurData = 0;
      }
    }
  }
}

// Midi OUT

void serial_MidiSend1(uint8_t b0) {
  sdPut(&SDMIDI, b0);
}

void serial_MidiSend2(uint8_t b0, uint8_t b1) {
  unsigned char tx[2];
  tx[0] = b0;
  tx[1] = b1;
  sdWrite(&SDMIDI, tx, 2);
}

void serial_MidiSend3(uint8_t b0, uint8_t b1, uint8_t b2) {
  unsigned char tx[3];
  tx[0] = b0;
  tx[1] = b1;
  tx[2] = b2;
  sdWrite(&SDMIDI, tx, 3);
}

int serial_MidiGetOutputBufferPending(void) {
  return chOQGetFullI(&SDMIDI.oqueue);
}

// Midi UART...
static const SerialConfig sdMidiCfg = {31250, // baud
    0, 0, 0};

static WORKING_AREA(waThreadMidi, 256) __attribute__ ((section (".ccmramend")));

__attribute__((noreturn))
  static msg_t ThreadMidi(void *arg) {
  (void)arg;
#if CH_USE_REGISTRY
  chRegSetThreadName("midi");
#endif
  while (1) {
    char ch;
    ch = sdGet(&SDMIDI);
    serial_MidiInByteHandler(ch);
  }
}

void serial_midi_init(void) {
  /*
   * Activates the serial driver 2 using the driver default configuration.
   * PA2(TX) and PA3(RX) are routed to USART2.
   */
#ifdef BOARD_AXOLOTI_V05
  // RX
  palSetPadMode(GPIOG, 9, PAL_MODE_ALTERNATE(8) | PAL_MODE_INPUT_PULLUP);
  // TX
  palSetPadMode(GPIOG, 14, PAL_MODE_ALTERNATE(8) | PAL_STM32_OTYPE_OPENDRAIN);
#else
  // RX
  palSetPadMode(GPIOB, 7, PAL_MODE_ALTERNATE(7)|PAL_MODE_INPUT_PULLUP);
  // TX
  palSetPadMode(GPIOB, 6, PAL_MODE_ALTERNATE(7)|PAL_STM32_OTYPE_OPENDRAIN);
#endif
  sdStart(&SDMIDI, &sdMidiCfg);
  chThdCreateStatic(waThreadMidi, sizeof(waThreadMidi), NORMALPRIO, ThreadMidi,
                    NULL);
}
