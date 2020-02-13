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
#include "midi_decoder.h"
#include "midi_encoder.h"
#include "midi_din.h"
#include "patch.h"


midi_routing_t midi_din_inputmap = {
		.name = "DIN",
		.nports = 1,
		.bmvports = {
				0b0000000000000001
		}
};

midi_routing_t midi_din_outputmap = {
			.name = "DIN",
			.nports = 1,
			.bmvports = {
					0b0000000000000001
			}
};

static void dispatch_midi_input(midi_message_t midi_msg) {
	  int portmap = midi_din_inputmap.bmvports[0];
	  midi_input_dispatch(portmap, midi_msg);
}

static midi_decoder_state_t din_midi_decoder = {
		.midi_rcv_cb = dispatch_midi_input
};

// Midi OUT

static void midi_din_send(midi_message_t midimsg) {
  // TODO: running status
  // TODO: skip other messages when sysex is in progress
	int l = midi_encoder_get_length(midimsg);
	sdWrite(&SDMIDI, &midimsg.bytes.b0, l);
}

int midi_din_GetOutputBufferPending(void) {
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
    midi_decoder_process(&din_midi_decoder, ch);
  }
}

midi_output_buffer_t midi_din_output;

static THD_WORKING_AREA(waThreadMidiOut, 256);
static THD_FUNCTION(ThreadMidiOut, arg) {
	(void) arg;
	chRegSetThreadName("midi_din_out");
	while (1) {
		eventmask_t evt = chEvtWaitOne(1);
		(void) evt;
		midi_message_t m;
		msg_t r;
		r = midi_output_buffer_get(&midi_din_output, &m);
		while (r == MSG_OK ) {
			midi_din_send(m);
			r = midi_output_buffer_get(&midi_din_output, &m);
		}
	}
}

static thread_t * thd_midi_din_writer;

static void notify(void *obj) {
  chEvtSignal(thd_midi_din_writer,1);
}

void midi_din_init(void) {
  /*
   * Activates the serial driver 2 using the driver default configuration.
   * PA2(TX) and PA3(RX) are routed to USART2.
   */

  load_midi_routing(&midi_din_inputmap, in);
  load_midi_routing(&midi_din_outputmap, out);

  // RX
  palSetPadMode(GPIOG, 9, PAL_MODE_ALTERNATE(8) | PAL_MODE_INPUT_PULLUP);
  // TX
  palSetPadMode(GPIOG, 14, PAL_MODE_ALTERNATE(8) | PAL_STM32_OTYPE_OPENDRAIN);

  sdStart(&SDMIDI, &sdMidiCfg);
  chThdCreateStatic(waThreadMidiIn, sizeof(waThreadMidiIn), NORMALPRIO, ThreadMidiIn,
                    NULL);
  thd_midi_din_writer = chThdCreateStatic(waThreadMidiOut, sizeof(waThreadMidiOut), NORMALPRIO, ThreadMidiOut,
                    NULL);
  midi_output_buffer_objinit(&midi_din_output, notify);
}
