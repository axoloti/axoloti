/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
#ifndef __MIDI_H
#define __MIDI_H

#include <stdint.h>

// MidiDefs.h
// according to MIDI standard

// MIDI Status bytes

#define MIDI_NOTE_OFF			0x80
#define MIDI_NOTE_ON			0x90
#define MIDI_POLY_PRESSURE		0xa0
#define MIDI_CONTROL_CHANGE		0xb0
#define MIDI_PROGRAM_CHANGE		0xc0
#define MIDI_CHANNEL_PRESSURE	0xd0
#define MIDI_PITCH_BEND			0xe0
#define MIDI_SYSEX_START		0xf0
#define MIDI_MTC				0xf1
#define MIDI_SONG_POSITION		0xf2
#define MIDI_SONG_SELECT		0xf3
#define MIDI_TUNE_REQUEST		0xf6
#define MIDI_SYSEX_END			0xf7
#define MIDI_RESET				0xff //	0xff never used as reset in a MIDIMessage
#define MIDI_META_EVENT			0xff //	0xff is for non MIDI messages
// MIDI Real Time Messages

#define MIDI_TIMING_CLOCK		0xf8
#define MIDI_MEASURE_END		0xf9 // proposed measure end byte
#define MIDI_START				0xfa
#define MIDI_CONTINUE			0xfb
#define MIDI_STOP				0xfc
#define MIDI_ACTIVE_SENSE		0xfe 

// Controller Numbers

#define MIDI_C_LSB				0x20 // add this to a non-switch controller to access the LSB.
#define MIDI_C_GM_BANK			0x00 // general midi bank select
#define MIDI_C_MODULATION		0x01 // modulation
#define MIDI_C_BREATH			0x02 // breath controller
#define MIDI_C_FOOT				0x04 // foot controller
#define MIDI_C_PORTA_TIME		0x05 // portamento time
#define MIDI_C_DATA_ENTRY		0x06 // data entry value
#define MIDI_C_MAIN_VOLUME		0x07 // main volume control
#define MIDI_C_BALANCE			0x08 // balance control
#define MIDI_C_PAN				0x0a // panpot stereo control
#define MIDI_C_EXPRESSION		0x0b // expression control
#define MIDI_C_GENERAL_1		0x10 // general purpose controller 1
#define MIDI_C_GENERAL_2		0x11 // general purpose controller 2
#define MIDI_C_GENERAL_3		0x12 // general purpose controller 3
#define MIDI_C_GENERAL_4		0x13 // general purpose controller 4
#define MIDI_C_DAMPER			0x40 // hold pedal (sustain)
#define MIDI_C_PORTA			0x41 // portamento switch
#define MIDI_C_SOSTENUTO		0x42 // sostenuto switch
#define MIDI_C_SOFT_PEDAL		0x43 // soft pedal
#define MIDI_C_HOLD_2			0x45 // hold pedal 2
#define MIDI_C_TIMBRE			0x4a // timbre
#define MIDI_C_GENERAL_5		0x50 // general purpose controller 5
#define MIDI_C_GENERAL_6		0x51 // general purpose controller 6
#define MIDI_C_GENERAL_7		0x52 // general purpose controller 7
#define MIDI_C_GENERAL_8		0x53 // general purpose controller 8
#define MIDI_C_EFFECT_DEPTH		0x5b // external effects depth
#define MIDI_C_TREMELO_DEPTH	0x5c // tremelo depth
#define MIDI_C_CHORUS_DEPTH		0x5d // chorus depth
#define MIDI_C_CELESTE_DEPTH	0x5e // celeste (detune) depth
#define MIDI_C_PHASER_DEPTH		0x5f // phaser effect depth 
#define MIDI_C_DATA_INC			0x60 // increment data value
#define MIDI_C_DATA_DEC			0x61 // decrement data value
#define MIDI_C_NONRPN_LSB		0x62 // non registered parameter LSB
#define MIDI_C_NONRPN_MSB		0x63 // non registered parameter MSB
#define MIDI_C_RPN_LSB			0x64 // registered parameter LSB
#define MIDI_C_RPN_MSB			0x65 // registered parameter MSB
#define MIDI_C_RESET			0x79 // reset all controllers
#define MIDI_C_LOCAL			0x7a // local control on/off
#define MIDI_C_ALL_NOTES_OFF	0x7b // all notes off
#define MIDI_C_OMNI_OFF			0x7c // omni off all notes off
#define MIDI_C_OMNI_ON			0x7d // omni on all notes off
#define MIDI_C_MONO				0x7e // mono on all notes off
#define MIDI_C_POLY				0x7f // poly on all notes off

typedef enum
{
    MIDI_DEVICE_OMNI = 0,          // for filtering
    MIDI_DEVICE_DIN,             // MIDI_DIN
    MIDI_DEVICE_USB_DEVICE,      // Board acting as Midi device over MicroUSB 
    MIDI_DEVICE_USB_HOST,        // Board hosting devices vid USB host port
    MIDI_DEVICE_DIGITAL_X1,      // x1 pins - not implemented
    MIDI_DEVICE_DIGITAL_X2,       // x2 pins - not implemented
    MIDI_DEVICE_INTERNAL = 0x0F     // internal (to the board) midi
} midi_device_t ;

// midi port, from 1  = OMNI for filtering and internal messages
#define MIDI_PORT_OMNI 0

void midi_init(void);
void MidiInMsgHandler(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2);


void MidiSend1(midi_device_t dev, uint8_t port, uint8_t b0);
void MidiSend2(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1);
void MidiSend3(midi_device_t dev, uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2);
void MidiSendSysEx(midi_device_t dev, uint8_t port, uint8_t bytes[], uint8_t len);

int  MidiGetOutputBufferPending(midi_device_t dev);
int  MidiGetOutputBufferAvailable(midi_device_t dev);


#endif
