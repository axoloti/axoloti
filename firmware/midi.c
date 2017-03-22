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
#include "usbh_midi_core.h"
#include "midi_usb.h"
#include "patch.h"
#include "midi_buffer.h"
#include "usbh_midi_core.h"

midi_input_buffer_t midi_input_buffer;

// output routing table
midi_output_routing_t midi_output_routing_table[MIDI_VPORTS][MIDI_TARGETS_PER_VPORT] = {
// virtual output port 0 to DIN
		{{MIDI_DEVICE_DIN, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 1 to USB device (PC)
		{{MIDI_DEVICE_USB_DEVICE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 2 to USB host (usb-midi device on host port), port 0
		{{MIDI_DEVICE_USB_HOST, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 3 to everything DIN, USBD port 0, USBH
		{{MIDI_DEVICE_DIN, 0},{MIDI_DEVICE_USB_DEVICE, 0},{MIDI_DEVICE_USB_HOST, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 4 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 5 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 6 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 7 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 8 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 9 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 10 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 11 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 12 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 13 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// virtual output port 14 to nowhere
		{{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}, {MIDI_DEVICE_OUTPUTMAP_NONE, 0}},
// let's use virtual output port 15 for midi clock, to everywhere
		{{MIDI_DEVICE_DIN, 0},{MIDI_DEVICE_USB_DEVICE, 0},{MIDI_DEVICE_USB_HOST, 0},{MIDI_DEVICE_OUTPUTMAP_NONE, 0}}
};

void MidiSendVirtual(midi_message_t m) {
	int vport = m.fields.port;
	midi_output_routing_t *port_routing = &midi_output_routing_table[vport][0];
	int i;
	for(i=0;i<MIDI_TARGETS_PER_VPORT;i++) {
		switch (port_routing->midi_device_t) {
		case MIDI_DEVICE_OUTPUTMAP_NONE:
			break;
		case MIDI_DEVICE_DIN:
			serial_MidiSend(m);
		break;
		case MIDI_DEVICE_USB_DEVICE:
			m.fields.port = port_routing->port;
			midi_output_buffer_put(&midi_output_usbd,m);
		break;
		case MIDI_DEVICE_USB_HOST:
			m.fields.port = port_routing->port;
			midi_output_buffer_put(&midi_output_usbh,m);
			break;
		}
		port_routing++;
	}
}

void MidiSendSysExVirtual(uint8_t port, uint8_t bytes[], uint8_t len) {
	// TODO: implement
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
	int virtualport = dev;
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.ph = Midi_calcPH(virtualport,b0);
	MidiSendVirtual(m);
}

void MidiSend2(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1) {
	int virtualport = dev;
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.ph = Midi_calcPH(virtualport,b0);
	MidiSendVirtual(m);
}

void MidiSend3(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
	int virtualport = dev;
	volatile midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = b2;
	m.bytes.ph = Midi_calcPH(virtualport,b0);
	MidiSendVirtual(m);
}

void MidiSendSysEx(midi_device_t dev, uint8_t port, uint8_t bytes[], uint8_t len) {
	int virtualport = dev;
	MidiSendSysExVirtual(virtualport,bytes,len);
}

void midi_init(void) {
    midi_input_buffer_objinit(&midi_input_buffer);
    serial_midi_init();
    usbh_midi_init();
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
