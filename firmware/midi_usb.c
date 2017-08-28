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

/**
 * @file    midi_usb.c
 * @brief   Midi USB Driver code.
 *
 * @addtogroup MIDI_USB
 * @{
 */

#include "ch.h"
#include "hal.h"
#include "midi.h"
#include "midi_routing.h"
#include "midi_usb.h"
#include "usbcfg.h"
#include "midi_buffer.h"

midi_output_buffer_t midi_output_usbd;

midi_input_remap_t midi_inputmap_usbd = {
		"USB device",
		1,
		{{MIDI_DEVICE_USB_DEVICE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE}}
};

thread_t * thd_midi_Writer;
thread_t * thd_midi_Reader;


static THD_WORKING_AREA(waMidiWriter, 128);
static THD_FUNCTION(MidiWriter, arg) {
  chRegSetThreadName("usbdmidiw");
  // TODO: implement...
	while (true) {
		eventmask_t evt = chEvtWaitOne(1);
		(void)evt;
		static midi_message_t outbuf[16];
		int s;
		midi_message_t *m = &outbuf[0];
		for(s=0;s<16;s++) {
			msg_t r = midi_output_buffer_get(&midi_output_usbd, m);
			if (r!=0) break;
			m++;
		}
		// buffer made, transmit
		if (s>0) {
			msg_t r = usbTransmit(&USBD1, USBD1_DATA_REQUEST_EP, (uint8_t *)outbuf, s*4);
			if (r!=MSG_OK) {
				chThdSleepMilliseconds(1000);
			}
		}
	}
}

static THD_WORKING_AREA(waMidiReader, 512);
static THD_FUNCTION(MidiReader, arg) {
	chRegSetThreadName("usbdmidir");
	while (true) {
		static midi_message_t midi_usbd_rxbuf[16];
		// fits 64 byte usb packet
		msg_t msg = usbReceive(&USBD1, USBD1_DATA_AVAILABLE_EP,
				(uint8_t *) midi_usbd_rxbuf, sizeof(midi_usbd_rxbuf));
		if (msg > 0) {
			int i;
			for (i = 0; i < msg / 4; i++) {
#if 0 // diagnostics
				LogTextMessage("MD %2X %2X %2X %2X",
						midi_usbd_rxbuf[i].bytes.ph,
						midi_usbd_rxbuf[i].bytes.b0,
						midi_usbd_rxbuf[i].bytes.b1,
						midi_usbd_rxbuf[i].bytes.b2);
#endif

		    	  int i=0;
		    	  for (i=0;i<MIDI_INPUT_REMAP_ENTRIES;i++) {
		    		  int virtual_port = midi_inputmap_usbd.portmap[0][i];
		    		  if (virtual_port == MIDI_DEVICE_INPUTMAP_NONE) break;
		    		  midi_usbd_rxbuf[i].fields.port = virtual_port;
					  midi_input_buffer_put(&midi_input_buffer, midi_usbd_rxbuf[i]);
		    	  }

			}
		} else {
			chThdSleepMilliseconds(1000);
		}
	}
}

void midi_usb_MidiSend1(uint8_t port, uint8_t b0) {

}

void midi_usb_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1) {
  // TODO: implement...
}

void midi_usb_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
  // TODO: implement...
}

static void notify(void *obj) {
  chEvtSignal(thd_midi_Writer,1);
}

void midi_usb_init(void) {
	midi_output_buffer_objinit(&midi_output_usbd, notify);
	thd_midi_Writer = chThdCreateStatic(waMidiWriter, sizeof(waMidiWriter), NORMALPRIO, MidiWriter, NULL);
	thd_midi_Reader =  chThdCreateStatic(waMidiReader, sizeof(waMidiReader), NORMALPRIO, MidiReader, NULL);
}


/** @} */
