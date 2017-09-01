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
		{0b00000010}
};

midi_output_routing_t midi_outputmap_usbd = {
			.name = "USB device",
			.nports = 1,
			.bmvports = {0b00000010}
};

static thread_t * thd_usbd_midi_out;
static thread_t * thd_usbd_midi_in;

static THD_WORKING_AREA(waUsbd_midi_out, 128);
static THD_FUNCTION(usbd_midi_out, arg) {
  chRegSetThreadName("usbd_midi_out");
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

static THD_WORKING_AREA(waUsbd_midi_in, 256);
static THD_FUNCTION(usbd_midi_in, arg) {
	chRegSetThreadName("usbd_midi_in");
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
	    		  int portmap = midi_inputmap_usbd.bmvports[0];
	    		  int v;
		    	  for (v=0;v<16;v++) {
		    		  if (portmap & 1) {
						  midi_usbd_rxbuf[i].fields.port = v;
						  midi_input_buffer_put(&midi_input_buffer, midi_usbd_rxbuf[i]);
		    		  }
		    		  portmap = portmap>>1;
		    	  }
			}
		} else {
			chThdSleepMilliseconds(1000);
		}
	}
}


static void notify(void *obj) {
  chEvtSignal(thd_usbd_midi_out,1);
}

void midi_usb_init(void) {
	midi_output_buffer_objinit(&midi_output_usbd, notify);
	thd_usbd_midi_out = chThdCreateStatic(waUsbd_midi_out, sizeof(waUsbd_midi_out), NORMALPRIO, usbd_midi_out, NULL);
	thd_usbd_midi_in =  chThdCreateStatic(waUsbd_midi_in, sizeof(waUsbd_midi_in), NORMALPRIO, usbd_midi_in, NULL);
}


/** @} */
