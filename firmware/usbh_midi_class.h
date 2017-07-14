#if 0
/*===========================================================================*/
/* External declarations.                                                    */
/*===========================================================================*/

extern const usbh_classdriverinfo_t usbhmidiClassDriverInfo;

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


/*
    ChibiOS - Copyright (C) 2006..2017 Giovanni Di Sirio
              Copyright (C) 2015..2017 Diego Ismirlian, (dismirlian (at) google's mail)
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

#ifndef USBH_CUSTOM_H_
#define USBH_CUSTOM_H_

#include "hal_usbh.h"

#if HAL_USE_USBH && HAL_USBH_USE_ADDITIONAL_CLASS_DRIVERS

/*===========================================================================*/
/* Driver pre-compile time settings.                                         */
/*===========================================================================*/
#define USBH_MIDI_CLASS_MAX_INSTANCES                  2
#define USBH_MIDI_DEBUG_ENABLE_TRACE                    0
#define USBH_MIDI_DEBUG_ENABLE_INFO                     1
#define USBH_MIDI_DEBUG_ENABLE_WARNINGS                 1
#define USBH_MIDI_DEBUG_ENABLE_ERRORS                   1


#define USB_AUDIO_CLASS 0x01
#define USB_MIDISTREAMING_SubCLASS 0x03

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

extern USBHMIDIDriver USBHMIDID[USBH_MIDI_CLASS_MAX_INSTANCES];

#ifdef __cplusplus
extern "C" {
#endif
	/* API goes here */

#ifdef __cplusplus
}
#endif

#endif

#endif /* USBH_CUSTOM_H_ */



