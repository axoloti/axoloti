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

/**
 * (based on work by Xavier Halgand)
 */

/* Define to prevent recursive  ----------------------------------------------*/
#ifndef __USBH_MIDI_CORE_H
#define __USBH_MIDI_CORE_H

/* Includes ------------------------------------------------------------------*/
#include "stdint.h"
#include "midi_buffer.h"
#include "usbh_midi_core.h"

extern midi_output_buffer_t midi_output_usbh;

// external midi interface
void usbh_midi_init(void);
void usbh_midi_reset_buffer(void);
void usbh_MidiSend1(uint8_t port, uint8_t b0);
void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1);
void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2);
void usbh_MidiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len);

int  usbh_MidiGetOutputBufferPending(void);
int  usbh_MidiGetOutputBufferAvailable(void);


#endif /* __USBH_MIDI_CORE_H */

/************************ ****************** *****END OF FILE****/

