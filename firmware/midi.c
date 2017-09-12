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
#include "midi_usbh.h"
#include "midi_usb.h"
#include "patch.h"
#include "midi_buffer.h"
#include "usbh_conf.h"

midi_input_buffer_t midi_input_buffer;

void MidiSendVirtual(midi_message_t m) {
	int vport = m.fields.port;
	int vportmask = 1<<vport;
	if (midi_outputmap_din.bmvports[0] & vportmask) {
		midi_output_buffer_put(&midi_output_din, m);
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
}

#define CIN_SYSEX_START_CONTINUE 0x04
#define CIN_SYSEX_END_1 0x05
#define CIN_SYSEX_END_2 0x06
#define CIN_SYSEX_END_3 0x07


void MidiSendSysExVirtual(uint8_t port, uint8_t bytes[], uint8_t len) {
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
		MidiSendVirtual(contm);
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

	MidiSendVirtual(endm);
}

// pack header CN | CIN
inline uint8_t Midi_calcPH(uint8_t port, uint8_t b0) {
    // CIN for everyting except sysex
    uint8_t cin  = (b0 & 0xF0 ) >> 4;
    uint8_t ph = ((( port - 1) & 0x0F) << 4)  | cin;
    return ph;
}

// legacy API, ignore port arg, use dev number as virtual port number
void MidiSend1(midi_device_t dev, uint8_t   port, uint8_t b0) {
	int virtualport = port;
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.ph = Midi_calcPH(virtualport,b0);
	MidiSendVirtual(m);
}

void MidiSend2(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1) {
	int virtualport = port;
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.ph = Midi_calcPH(virtualport,b0);
	MidiSendVirtual(m);
}

void MidiSend3(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
	int virtualport = port;
	volatile midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = b2;
	m.bytes.ph = Midi_calcPH(virtualport,b0);
	MidiSendVirtual(m);
}

void MidiSendSysEx(midi_device_t dev, uint8_t port, uint8_t bytes[], uint8_t len) {
	int virtualport = port-1;
	MidiSendSysExVirtual(virtualport,bytes,len);
}

void midi_init(void) {
    midi_input_buffer_objinit(&midi_input_buffer);
    serial_midi_init();
}

int  MidiGetOutputBufferPending(midi_device_t dev)
{
	// low priority TODO: implement
	// for a virtual output device, we need to find the
	// maximum of bytes pending across all mapped real outputs
	return 0;
}

int  MidiGetOutputBufferAvailable(midi_device_t dev)
{
	// low priority TODO: implement
	// for a virtual output device, we need to find the
	// minimum of buffer bytes available across all mapped real outputs
	return 0;
}
