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


#ifndef USBH_MIDI_H_
#define USBH_MIDI_H_

#include "hal_usbh.h"


/* TODO:
 *
 */


/*===========================================================================*/
/* Driver pre-compile time settings.                                         */
/*===========================================================================*/

#define HAL_USBH_USE_MIDI TRUE
#define HAL_USBHMIDI_MAX_INSTANCES 2
#define USBHMIDI_DEBUG_ENABLE_TRACE                    0
#define USBHMIDI_DEBUG_ENABLE_INFO                     1
#define USBHMIDI_DEBUG_ENABLE_WARNINGS                 1
#define USBHMIDI_DEBUG_ENABLE_ERRORS                   1

/*===========================================================================*/
/* Derived constants and error checks.                                       */
/*===========================================================================*/


/*===========================================================================*/
/* Driver data structures and types.                                         */
/*===========================================================================*/

typedef enum {
	USBHMIDI_STATE_UNINIT = 0,
	USBHMIDI_STATE_STOP = 1,
	USBHMIDI_STATE_ACTIVE = 2,
	USBHMIDI_STATE_READY = 3
} usbhmidi_state_t;


#define USB_AUDIO_CLASS 0x01
#define USB_MIDISTREAMING_SubCLASS 0x03

typedef struct USBHMIDIDriver USBHMIDIDriver;
typedef struct USBHMIDIConfig USBHMIDIConfig;

typedef void (*usbhmidi_report_callback)(USBHMIDIDriver *midip, uint16_t len);

struct USBHMIDIConfig {
	usbhmidi_report_callback cb_report;
	void *report_buffer;
	uint16_t report_len;
};

struct USBHMIDIDriver {
	/* inherited from abstract class driver */
	_usbh_base_classdriver_data

	usbh_ep_t epin;
	usbh_ep_t epout;

	uint8_t ifnum;

	usbhmidi_state_t state;

	usbh_urb_t in_urb;

	const USBHMIDIConfig *config;
};


/*===========================================================================*/
/* Driver macros.                                                            */
/*===========================================================================*/


/*===========================================================================*/
/* External declarations.                                                    */
/*===========================================================================*/

extern USBHMIDIDriver USBHMIDID[HAL_USBHMIDI_MAX_INSTANCES];

#ifdef __cplusplus
extern "C" {
#endif
	/* MIDI Driver */
	void usbhmidiObjectInit(USBHMIDIDriver *midip);

	static inline usbhmidi_state_t usbhmidiGetState(USBHMIDIDriver *midip) {
		return midip->state;
	}

	void usbhmidiStart(USBHMIDIDriver *midip, const USBHMIDIConfig *cfg);

	/* global initializer */
	void usbhmidiInit(void);
#ifdef __cplusplus
}
#endif

#endif


/************************ ****************** *****END OF FILE****/

