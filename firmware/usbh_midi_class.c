/**
 ******************************************************************************
 * @file    usbh_midi_class.c
 * @author  Johannes Taelman
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


#include "hal.h"

#if !HAL_USE_USBH
#error "USBHMIDI needs USBH"
#endif

#include <string.h>
#include "usbh_midi_class.h"
#include "usbh/internal.h"

#if USBH_MIDI_DEBUG_ENABLE_TRACE
#define udbgf(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define udbg(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define udbgf(f, ...)  do {} while(0)
#define udbg(f, ...)   do {} while(0)
#endif

#if USBH_MIDI_DEBUG_ENABLE_INFO
#define uinfof(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define uinfo(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uinfof(f, ...)  do {} while(0)
#define uinfo(f, ...)   do {} while(0)
#endif

#if USBH_MIDI_DEBUG_ENABLE_WARNINGS
#define uwarnf(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define uwarn(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uwarnf(f, ...)  do {} while(0)
#define uwarn(f, ...)   do {} while(0)
#endif

#if USBH_MIDI_DEBUG_ENABLE_ERRORS
#define uerrf(f, ...)  usbDbgPrintf(f, ##__VA_ARGS__)
#define uerr(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uerrf(f, ...)  do {} while(0)
#define uerr(f, ...)   do {} while(0)
#endif


/*===========================================================================*/
/* USB Class driver loader for MIDI Class Example				 		 	 */
/*===========================================================================*/

USBHMIDIDriver USBHMIDID[USBH_MIDI_CLASS_MAX_INSTANCES];

static void _init(void);
static usbh_baseclassdriver_t *_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem);
static void _unload(usbh_baseclassdriver_t *drv);

static const usbh_classdriver_vmt_t class_driver_vmt = {
	_init,
	_load,
	_unload
};

const usbh_classdriverinfo_t usbhMidiClassDriverInfo = {
	"MIDI", &class_driver_vmt
};

static usbh_baseclassdriver_t *_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem) {
	int i;
	USBHMIDIDriver *midip;
	(void)dev;

	if (_usbh_match_descriptor(descriptor, rem, USBH_DT_INTERFACE,
			USB_AUDIO_CLASS, USB_MIDISTREAMING_SubCLASS, -1) != HAL_SUCCESS)
		return NULL;

	const usbh_interface_descriptor_t * const ifdesc = (const usbh_interface_descriptor_t *)descriptor;

	if ((ifdesc->bAlternateSetting != 0)
			|| (ifdesc->bNumEndpoints < 1)) {
		return NULL;
	}

	/* alloc driver */
	for (i = 0; i < USBH_MIDI_CLASS_MAX_INSTANCES; i++) {
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
	midip->ifnum = ifdesc->bInterfaceNumber;

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
//	if (midip->epin.status != USBH_EPSTATUS_CLOSED) {
//		goto deinit;
//	}
//	if (midip->epout.status != USBH_EPSTATUS_CLOSED) {
//		goto deinit;
//	}

	midip->state = USBHMIDI_STATE_ACTIVE;

	return (usbh_baseclassdriver_t *)midip;

}

static void _unload(usbh_baseclassdriver_t *drv) {
	(void)drv;
}

static void _object_init(USBHMIDIDriver *midip) {
	osalDbgCheck(midip != NULL);
	memset(midip, 0, sizeof(*midip));
	midip->state = USBHMIDI_STATE_STOP;
}

static void _init(void) {
	uint8_t i;
	for (i = 0; i < USBH_MIDI_CLASS_MAX_INSTANCES; i++) {
		_object_init(&USBHMIDID[i]);
	}
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

	/* open the bulk IN/OUT endpoints */
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
