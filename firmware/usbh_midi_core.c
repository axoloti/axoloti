/**
 ******************************************************************************
 * @file    usbh_midi_core.c
 * @author  Johannes Taelman (based on work by Xavier Halgand)
 * @version
 * @date
 * @brief   Very basic driver for USB Host MIDI class.
 *
 * @verbatim
 *
 * @endverbatim
 *
 ******************************************************************************
 *
 *
 ******************************************************************************
 */

/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

/* Includes ------------------------------------------------------------------*/
#include "usbh_midi_core_lld.h"
#include "usbh_midi_core.h"
#include "ch.h"
#include "axoloti_board.h"
#include "exceptions.h"
#include "midi.h"
#include "midi_routing.h"
#include "midi_buffer.h"

midi_output_buffer_t midi_output_usbh;

// map 16 usb host ports to 16 virtual inputs
// this map squeezes all midi ports on the usb host connector onto one...
midi_input_remap_t midi_inputmap_usbh[16] = {
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
		{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE}
};

#if 0
USB_Setup_TypeDef MIDI_Setup;
#endif

#define USBH_DbgLog(x)

#define MIDI_MIN_READ_POLL 1
#define MIDI_MIN_WRITE_POLL 1

static void notify(void * obj) {
	// TODO: we're currently polling for USBH transmission
	chSysHalt("usbh notify");
}

void usbh_midi_init(void)
{
  midi_output_buffer_objinit(&midi_output_usbh, notify);
  midi_output_buffer_deinit(&midi_output_usbh);
}

// pack header CN | CIN
inline uint8_t USBMidi_calcPH(uint8_t port, uint8_t b0) {
    uint8_t cin  = (b0 & 0xF0 ) >> 4;
    uint8_t ph = ((( port - 1) & 0x0F) << 4)  | cin;
    return ph;
}


void usbh_MidiSend1(uint8_t port, uint8_t b0) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = 0;
	m.bytes.b2 = 0;
	m.bytes.ph = USBMidi_calcPH(port, b0);
	midi_output_buffer_put(&midi_output_usbh,m);
    USBH_DbgLog("usbh_MidiSend1");
}

void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = 0;
	m.bytes.ph = USBMidi_calcPH(port, b0);
	midi_output_buffer_put(&midi_output_usbh,m);
    USBH_DbgLog("usbh_MidiSend2");
}

void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = 0;
	m.bytes.ph = USBMidi_calcPH(port, b0);
	midi_output_buffer_put(&midi_output_usbh,m);
    USBH_DbgLog("usbh_MidiSend3");
}

#define CIN_SYSEX_START 0x04
#define CIN_SYSEX_END_1 0x05
#define CIN_SYSEX_END_2 0x06
#define CIN_SYSEX_END_3 0x07

void usbh_MidiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len) {
#if 0 // TODO: needs re-implementation
	if (send_ring_buffer.write_ptr + 1 == 0) return;
    USBH_DbgLog("usbh_MidiSysEx %i",len);
    uint8_t next = send_ring_buffer.write_ptr;

    uint8_t cn = ((( port - 1) & 0x0F) << 4);
    uint8_t cin = CIN_SYSEX_START;
    uint8_t ph = cin | cn;
    int i = 0;
    for(i = 0; i< (len - 3); i += 3) {
        next = (next + 1) % RING_BUFFER_SIZE;
        // later do this up front... but read_ptr may be changing
        if(next == send_ring_buffer.read_ptr) {
            report_usbh_midi_ringbuffer_overflow();
            return;
        }

        USBH_DbgLog("usbh_MidiSysEx start %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = bytes[i + 2];
    }

    int res = len - i;

    // end the sysex message, also handles special cases 2/3 bytes
    next = (next + 1) % RING_BUFFER_SIZE;
    if(next == send_ring_buffer.read_ptr) {
        report_usbh_midi_ringbuffer_overflow();
        return;
    }

    if (res == 1) {
        cin = CIN_SYSEX_END_1;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 1 %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = 0;
        send_ring_buffer.event[next].data[3] = 0;
    } else if (res == 2) {
        cin = CIN_SYSEX_END_2;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 2 %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = 0;
    } else if (res == 3) {
        cin = CIN_SYSEX_END_3;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 3 %i, %i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = bytes[i + 2];
    }

    send_ring_buffer.write_ptr=next;
#endif
}

int  usbh_MidiGetOutputBufferPending(void) {
	return midi_output_buffer_getpending(&midi_output_usbh);
}

int  usbh_MidiGetOutputBufferAvailable(void) {
	return midi_output_buffer_get_available(&midi_output_usbh);
}

/** @defgroup USBH_MIDI_CORE_Private_Variables
 * @{
 */


#include "hal.h"

//#if HAL_USBH_USE_MIDI

#if !HAL_USE_USBH
#error "USBHMIDI needs USBH"
#endif

#include <string.h>
#include "usbh_midi_core_lld.h"
#include "usbh/internal.h"

#if USBHMIDI_DEBUG_ENABLE_TRACE
#define udbgf(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define udbg(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define udbgf(f, ...)  do {} while(0)
#define udbg(f, ...)   do {} while(0)
#endif

#if USBHMIDI_DEBUG_ENABLE_INFO
#define uinfof(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define uinfo(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uinfof(f, ...)  do {} while(0)
#define uinfo(f, ...)   do {} while(0)
#endif

#if USBHMIDI_DEBUG_ENABLE_WARNINGS
#define uwarnf(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define uwarn(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uwarnf(f, ...)  do {} while(0)
#define uwarn(f, ...)   do {} while(0)
#endif

#if USBHMIDI_DEBUG_ENABLE_ERRORS
#define uerrf(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define uerr(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uerrf(f, ...)  do {} while(0)
#define uerr(f, ...)   do {} while(0)
#endif



#define USBH_MIDI_REQ_GET_REPORT		0x01
#define USBH_MIDI_REQ_GET_IDLE		0x02
#define USBH_MIDI_REQ_GET_PROTOCOL	0x03
#define USBH_MIDI_REQ_SET_REPORT		0x09
#define USBH_MIDI_REQ_SET_IDLE		0x0A
#define USBH_MIDI_REQ_SET_PROTOCOL	0x0B





void usbh_midi_dispatch(midi_message_t m) {
	udbgf("M %8X", m.word);
	int8_t *inputmap = midi_inputmap_usbh[m.fields.port];
	int i=0;
	for (i=0;i<MIDI_INPUT_REMAP_ENTRIES;i++) {
		int virtual_port = *inputmap;
		if (virtual_port == MIDI_DEVICE_INPUTMAP_NONE) break;
		m.fields.port = virtual_port;
		midi_input_buffer_put(&midi_input_buffer, m);
		inputmap++;
	}
}




/*===========================================================================*/
/* USB Class driver loader for MIDI								 		 	 */
/*===========================================================================*/

USBHMIDIDriver USBHMIDID[HAL_USBHMIDI_MAX_INSTANCES];

static usbh_baseclassdriver_t *_midi_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem);
static void _midi_unload(usbh_baseclassdriver_t *drv);

static const usbh_classdriver_vmt_t class_driver_vmt = {
	_midi_load,
	_midi_unload
};

const usbh_classdriverinfo_t usbhmidiClassDriverInfo = {
	USB_AUDIO_CLASS, USB_MIDISTREAMING_SubCLASS, -1, "MIDI", &class_driver_vmt
};

static usbh_baseclassdriver_t *_midi_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem) {
	int i;
	USBHMIDIDriver *midip;

	if ((rem < descriptor[0]) || (descriptor[1] != USBH_DT_INTERFACE))
		return NULL;

	const usbh_interface_descriptor_t * const ifdesc = (const usbh_interface_descriptor_t *)descriptor;

	if ((ifdesc->bAlternateSetting != 0)
			|| (ifdesc->bNumEndpoints < 1)) {
		return NULL;
	}


	/* alloc driver */
	for (i = 0; i < HAL_USBHMIDI_MAX_INSTANCES; i++) {
		if (USBHMIDID[i].dev == NULL) {
			midip = &USBHMIDID[i];
			goto alloc_ok;
		}
	}

	uwarn("Can't alloc MIDI driver");

	/* can't alloc */
	return NULL;

alloc_ok:
	/* initialize the driver's variables */
	midip->epin.status = USBH_EPSTATUS_UNINITIALIZED;
#if HAL_USBHMIDI_USE_INTERRUPT_OUT
	midip->epout.status = USBH_EPSTATUS_UNINITIALIZED;
#endif
	midip->ifnum = ifdesc->bInterfaceNumber;
	usbhEPSetName(&dev->ctrl, "MIDI[CTRL]");

	/* parse the configuration descriptor */
	if_iterator_t iif;
	generic_iterator_t iep;
	iif.iad = 0;
	iif.curr = descriptor;
	iif.rem = rem;
	for (ep_iter_init(&iep, &iif); iep.valid; ep_iter_next(&iep)) {
		const usbh_endpoint_descriptor_t *const epdesc = ep_get(&iep);
		if ((epdesc->bEndpointAddress & 0x80) && (epdesc->bmAttributes == USBH_EPTYPE_BULK)) {
			uinfof("BULK IN endpoint found: bEndpointAddress=%02x", epdesc->bEndpointAddress);
			usbhEPObjectInit(&midip->epin, dev, epdesc);
			usbhEPSetName(&midip->epin, "MIDI[IIN ]");
		} else if (((epdesc->bEndpointAddress & 0x80) == 0)
				&& (epdesc->bmAttributes == USBH_EPTYPE_BULK)) {
			uinfof("BULK OUT endpoint found: bEndpointAddress=%02x", epdesc->bEndpointAddress);
			usbhEPObjectInit(&midip->epout, dev, epdesc);
			usbhEPSetName(&midip->epout, "MIDI[IOUT]");
		} else {
			uinfof("unsupported endpoint found: bEndpointAddress=%02x, bmAttributes=%02x",
					epdesc->bEndpointAddress, epdesc->bmAttributes);
		}
	}
	if (midip->epin.status != USBH_EPSTATUS_CLOSED) {
		goto deinit;
	}

	midip->state = USBHMIDI_STATE_ACTIVE;

	return (usbh_baseclassdriver_t *)midip;

deinit:
	/* Here, the enpoints are closed, and the driver is unlinked */
	return NULL;
}

static void _midi_unload(usbh_baseclassdriver_t *drv) {
	(void)drv;
}

static void _in_cb(usbh_urb_t *urb) {
	USBHMIDIDriver *const midip = (USBHMIDIDriver *)urb->userData;
	switch (urb->status) {
	case USBH_URBSTATUS_OK:
		if (midip->config->cb_report) {
			midip->config->cb_report(midip, urb->actualLength);
		}
		break;
	case USBH_URBSTATUS_DISCONNECTED:
		uwarn("MIDI: URB IN disconnected");

		return;
	case USBH_URBSTATUS_TIMEOUT:
		//no data
		break;
	default:
		uerrf("MIDI: URB IN status unexpected = %d", urb->status);
		break;
	}
	usbhURBObjectResetI(&midip->in_urb);
	usbhURBSubmitI(&midip->in_urb);
}

void usbhmidiStart(USBHMIDIDriver *midip, const USBHMIDIConfig *cfg) {
	osalDbgCheck(midip && cfg);
	osalDbgCheck((midip->state == USBHMIDI_STATE_ACTIVE)
			|| (midip->state == USBHMIDI_STATE_READY));

	if (midip->state == USBHMIDI_STATE_READY)
		return;

	midip->config = cfg;

	/* init the URBs */
	usbhURBObjectInit(&midip->in_urb, &midip->epin, _in_cb, midip,
			cfg->report_buffer, cfg->report_len);

	/* open the int IN/OUT endpoints */
	usbhEPOpen(&midip->epin);
	usbhEPOpen(&midip->epout);

	osalSysLock();
	usbhURBSubmitI(&midip->in_urb);
	osalSysUnlock();

	midip->state = USBHMIDI_STATE_READY;
}

void usbhmidiStop(USBHMIDIDriver *midip) {
	osalDbgCheck((midip->state == USBHMIDI_STATE_ACTIVE)
			|| (midip->state == USBHMIDI_STATE_READY));

	if (midip->state != USBHMIDI_STATE_READY)
		return;

	osalSysLock();
	usbhEPCloseS(&midip->epin);
	usbhEPCloseS(&midip->epout);
	midip->state = USBHMIDI_STATE_ACTIVE;
	osalSysUnlock();
}

void usbhmidiObjectInit(USBHMIDIDriver *midip) {
	osalDbgCheck(midip != NULL);
	memset(midip, 0, sizeof(*midip));
	midip->info = &usbhmidiClassDriverInfo;
	midip->state = USBHMIDI_STATE_STOP;
}

void usbhmidiInit(void) {
	uint8_t i;
	for (i = 0; i < HAL_USBHMIDI_MAX_INSTANCES; i++) {
		usbhmidiObjectInit(&USBHMIDID[i]);
	}
}

//#endif


