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
#include "midi_usb.h"
#include "usbcfg.h"


thread_t * thd_midi_Writer;
thread_t * thd_midi_Reader;


static THD_WORKING_AREA(waMidiWriter, 128);
static THD_FUNCTION(MidiWriter, arg) {
#if CH_USE_REGISTRY
  chRegSetThreadName("usbdmidiw");
#endif
  // TODO: implement...
	while (true) {
		eventmask_t evt = chEvtWaitOne(1);
		(void)evt;
		// read from queue, transmit
		// usbTransmit(&USBD1, USBD1_DATA_REQUEST_EP, data, size);
	}
}

uint8_t midi_usbd_rxbuf[64];

static THD_WORKING_AREA(waMidiReader, 128);
static THD_FUNCTION(MidiReader, arg) {
#if CH_USE_REGISTRY
  chRegSetThreadName("usbdmidir");
#endif
  // TODO: implement...
	while (true) {
	    msg_t msg = usbReceive(&USBD1, USBD1_DATA_AVAILABLE_EP,
	    		midi_usbd_rxbuf, sizeof (midi_usbd_rxbuf));
	    (void)msg;
	}
}

void midi_usb_MidiSend1(uint8_t port, uint8_t b0) {
  // TODO: implement...
}

void midi_usb_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1) {
  // TODO: implement...
}

void midi_usb_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
  // TODO: implement...
}

void midi_usb_init(void) {
	  // TODO: threads to receive/send usb midi
	//  thd_midi_Writer = chThdCreateStatic(waMidiWriter, sizeof(waMidiWriter), NORMALPRIO, MidiWriter, NULL);
	//  thd_midi_Reader =  chThdCreateStatic(waMidiReader, sizeof(waMidiReader), NORMALPRIO, MidiReader, NULL);
}


/** @} */
