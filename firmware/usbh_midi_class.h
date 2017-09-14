

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


#define USBH_MIDI_BUFSIZE 64


/*===========================================================================*/
/* Derived constants and error checks.                                       */
/*===========================================================================*/
/*===========================================================================*/
/* USB Class driver loader for MIDI Class                                    */
/*===========================================================================*/

#define USB_AUDIO_CLASS 0x01
#define USB_MIDISTREAMING_SubCLASS 0x03

/*
* Definitions from the USB_MIDI_ or usb_midi_ namespace come from:
* "Universal Serial Bus Class Definitions for MIDI Devices, Revision 1.0"
*/

/* Appendix A.1: MS Class-Specific Interface Descriptor Subtypes */
#define USB_MIDI_SUBTYPE_MS_DESCRIPTOR_UNDEFINED 0x00
#define USB_MIDI_SUBTYPE_MS_HEADER              0x01
#define USB_MIDI_SUBTYPE_MIDI_IN_JACK           0x02
#define USB_MIDI_SUBTYPE_MIDI_OUT_JACK          0x03
#define USB_MIDI_SUBTYPE_MIDI_ELEMENT           0x04

/* Appendix A.2: MS Class-Specific Endpoint Descriptor Subtypes */
#define USB_MIDI_SUBTYPE_DESCRIPTOR_UNDEFINED   0x00
#define USB_MIDI_SUBTYPE_MS_GENERAL             0x01

/* Appendix A.3: MS MIDI IN and OUT Jack types */
#define USB_MIDI_JACK_TYPE_UNDEFINED            0x00
#define USB_MIDI_JACK_TYPE_EMBEDDED             0x01
#define USB_MIDI_JACK_TYPE_EXTERNAL             0x02

/* Appendix A.5.1 Endpoint Control Selectors */
#define USB_MIDI_EP_CONTROL_UNDEFINED           0x00
#define USB_MIDI_ASSOCIATION_CONTROL            0x01



typedef struct {
    uint8_t bLength;
    uint8_t bDescriptorType;
    uint8_t bDescriptorSubtype;
    uint16_t bcdMSC;
    uint16_t wTotalLength;
} __attribute__((packed)) ms_interface_header_descriptor_t;

typedef struct {
    uint8_t bLength;
    uint8_t bDescriptorType;
    uint8_t bDescriptorSubType;
    uint8_t bNumEmbMIDIJack;
    uint8_t baAssocJackID[0];
} __attribute__((packed)) ms_bulk_data_endpoint_descriptor_t;

typedef struct {
    uint8_t bLength;
    uint8_t bDescriptorType;
    uint8_t bDescriptorSubtype;
    uint8_t bJackType;
    uint8_t bJackID;
    uint8_t iJack;
} midi_in_jack_descriptor_t;

typedef struct {
    uint8_t baSourceID;
    uint8_t BaSourcePin;
} midi_jack_descriptor_pin_t;

typedef struct {
    uint8_t bLength;
    uint8_t bDescriptorType;
    uint8_t bDescriptorSubtype;
    uint8_t bJackType;
    uint8_t bJackID;
    uint8_t bNrInputPins;
    midi_jack_descriptor_pin_t output_pins[0];
} midi_out_jack_descriptor_t;

typedef struct {
    uint8_t bLength;
    uint8_t bDescriptorType;
    uint8_t bDescriptorSubtype;
    uint8_t bElementID;
    uint8_t bNrInputPins;
    midi_jack_descriptor_pin_t input_pins[0];
} midi_element_descriptor_t;

typedef struct {
    uint8_t bNrOutputPins;
    uint8_t bInTerminalLink;
    uint8_t bOutTerminalLink;
    uint8_t bElCapsSize;
    uint8_t bmElementCaps[0];
} midi_element_descriptor_part2_t;



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

typedef void (*usbhmidi_report_callback)(USBHMIDIConfig *midic,  uint32_t *buf, int len);
typedef void (*usbhmidi_disconnect_callback)(USBHMIDIConfig *midic);

struct USBHMIDIConfig {
	usbhmidi_report_callback cb_report;
	usbhmidi_disconnect_callback cb_disconnect;
};

struct USBHMIDIDriver {
	/* inherited from abstract class driver */
	_usbh_base_classdriver_data

	usbh_ep_t epin;
	usbh_ep_t epout;

	uint8_t ifnum;

	usbhmidi_state_t state;

	usbh_urb_t in_urb;

	uint8_t report_buffer[USBH_MIDI_BUFSIZE];

	char name[32];
	int nInputPorts;
	int nOutputPorts;

	USBHMIDIConfig *config;

	semaphore_t sem;
};



void midi_class_init(void);
usbh_baseclassdriver_t *midi_class_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem);
void midi_class_unload(usbh_baseclassdriver_t *drv);


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
void usbhmidiStart(USBHMIDIDriver *midip);
msg_t usbhmidi_sendbuffer(USBHMIDIDriver *midip, uint8_t *buffer, int size);

#ifdef __cplusplus
}
#endif

#endif

#endif /* USBH_CUSTOM_H_ */



