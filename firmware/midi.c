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
#include "midi_din.h"
#include "midi_gpio.h"
#include "midi_usbh.h"
#include "midi_usb.h"
#include "patch.h"
#include "midi_buffer.h"

midi_input_buffer_t midi_input_buffer;

void midiSend(midi_message_t m) {
  int vport = m.fields.port;
  if (vport<8) {
    int vportmask = 1<<vport;
    if (midi_din_outputmap.bmvports[0] & vportmask) {
      midi_output_buffer_put(&midi_din_output, m);
    }
    if (midi_gpio_outputmap.bmvports[0] & vportmask) {
      midi_output_buffer_put(&midi_gpio_output, m);
    }
    int i;
    for (i=0;i<midi_outputmap_usbh1.nports;i++)
      if (midi_outputmap_usbh1.bmvports[i] & vportmask) {
        m.fields.port = i;
        midi_output_buffer_put(&USBHMIDIC[0].out_buffer, m);
      }
    for (i=0;i<midi_outputmap_usbh2.nports;i++)
      if (midi_outputmap_usbh2.bmvports[i] & vportmask) {
        m.fields.port = i;
        midi_output_buffer_put(&USBHMIDIC[1].out_buffer, m);
      }
    if (midi_outputmap_usbd.bmvports[0] & vportmask) {
      m.fields.port = 0;
      midi_output_buffer_put(&midi_output_usbd, m);
    }
  } else {
    // to input virtual port
    m.fields.port = vport - 8;
    midi_input_buffer_put(&midi_input_buffer, m);
  }
}

#define CIN_SYSEX_START_CONTINUE 0x04
#define CIN_SYSEX_END_1 0x05
#define CIN_SYSEX_END_2 0x06
#define CIN_SYSEX_END_3 0x07

void midiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len) {
    uint8_t cn = ((port & 0x0F) << 4);
    uint8_t cin = CIN_SYSEX_START_CONTINUE;
    uint8_t ph = cin | cn;
    int i = 0;
    for(i = 0; i < (len - 3); i += 3) {
		midi_message_t contm;
		contm.bytes.ph = ph;
		contm.bytes.b0 = bytes[i];
		contm.bytes.b1 = bytes[i + 1];
		contm.bytes.b2 = bytes[i + 2];
		midiSend(contm);
    }

    int res = len - i;

	midi_message_t endm;
    // end the sysex message, with 1, 2 or 3 bytes
    switch (res)  {
	    case 1 : {
	        cin = CIN_SYSEX_END_1;
	        ph = cin | cn;
			endm.bytes.ph = ph;
			endm.bytes.b0 = bytes[i];
			endm.bytes.b1 = 0;
			endm.bytes.b2 = 0;
			break;
		}
	    case 2 :  {
	        cin = CIN_SYSEX_END_2;
	        ph = cin | cn;
			endm.bytes.ph = ph;
			endm.bytes.b0 = bytes[i];
			endm.bytes.b1 = bytes[i + 1];
			endm.bytes.b2 = 0;
			break;
		}
	    case 3 :  {
	        cin = CIN_SYSEX_END_3;
	        ph = cin | cn;
			endm.bytes.ph = ph;
			endm.bytes.b0 = bytes[i];
			endm.bytes.b1 = bytes[i + 1];
			endm.bytes.b2 = bytes[i + 2];
			break;
		}
	}

	midiSend(endm);
}

void midi_input_dispatch(int32_t portmap, midi_message_t midi_msg) {
	int v;
	for (v=0;v<8;v++) {
		if (portmap & 1) {
			midi_msg.fields.port = v;
			midi_input_buffer_put(&midi_input_buffer, midi_msg);
		}
		portmap = portmap>>1;
	}
}

void midi_init(void) {
    midi_input_buffer_objinit(&midi_input_buffer);
    midi_din_init();
}
