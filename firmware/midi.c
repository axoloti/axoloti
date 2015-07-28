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
#include "usbh_midi_core.h"
#include "midi_usb.h"
#include "patch.h"


void MidiSend1(midi_device_t dev, uint8_t   port, uint8_t b0) {
    switch (dev) {
        case MIDI_DEVICE_DIN: {
            serial_MidiSend1(b0);
            break;
        }
        case MIDI_DEVICE_USB_HOST: {
            usbh_MidiSend1(port, b0);
            break;
        }
        case MIDI_DEVICE_INTERNAL: {
            MidiInMsgHandler(MIDI_DEVICE_INTERNAL, port, b0, 0, 0);
            break;
        }
        case MIDI_DEVICE_USB_DEVICE: {
            midi_usb_MidiSend1(port, b0);
            break;
        }
        default: {
            // nop
        }
    }
}

void MidiSend2(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1) {
    switch (dev) {
        case MIDI_DEVICE_DIN: {
            serial_MidiSend2(b0,b1);
            break;
        }
        case MIDI_DEVICE_USB_HOST: {
            usbh_MidiSend2(port, b0,b1);
            break;
        }
        case MIDI_DEVICE_INTERNAL: {
            MidiInMsgHandler(MIDI_DEVICE_INTERNAL, port, b0, b1, 0);
            break;
        }
        case MIDI_DEVICE_USB_DEVICE: {
            midi_usb_MidiSend2(port, b0, b1);
            break;
        }
        default: {
            // nop
        }
    }
}

void MidiSend3(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
    switch (dev) {
        case MIDI_DEVICE_DIN: {
            serial_MidiSend3(b0,b1,b2);
            break;
        }
        case MIDI_DEVICE_USB_HOST: {
            usbh_MidiSend3(port,b0,b1,b2);
            break;
        }
        case MIDI_DEVICE_INTERNAL: {
            MidiInMsgHandler(MIDI_DEVICE_INTERNAL, port, b0, b1, b2);
            break;
        }
        case MIDI_DEVICE_USB_DEVICE: {
            midi_usb_MidiSend3(port, b0, b1, b2);
            break;
        }
        default: {
            // nop
        }
    }
}

void MidiSendSysEx(midi_device_t dev, uint8_t port, uint8_t bytes[], uint8_t len) {
    switch (dev) {
        case MIDI_DEVICE_USB_HOST: {
            usbh_MidiSendSysEx(port,bytes,len);
            break;
        }
        default: {
            // nop
        }
    }
}

void midi_init(void) {
    serial_midi_init();
    usbh_midi_init();
}


int  MidiGetOutputBufferPending(midi_device_t dev)
{
    switch (dev) {
        case MIDI_DEVICE_DIN: {
            return serial_MidiGetOutputBufferPending();
        }
        case MIDI_DEVICE_USB_HOST: {
            return usbh_MidiGetOutputBufferPending();
        }
        default: {
            // not implemented 
            return 0;
        }
    }
}

int  MidiGetOutputBufferAvailable(midi_device_t dev)
{
    switch (dev) {
        case MIDI_DEVICE_USB_HOST: {
            return usbh_MidiGetOutputBufferAvailable();
        }
        default: {
            // not implemented 
            return 0;
        }
    }
}

