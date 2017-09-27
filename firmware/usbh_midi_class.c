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

#warning "Needs ChibiOS_16.1.8/community from https://github.com/ChibiOS/ChibiOS-Contrib,"
#warning "different than the one included in Chibios_16.1.8, "
#warning "otherwise compilation will fail on this file!"

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

USBHMIDIDriver USBHMIDID[USBH_MIDI_CLASS_MAX_INSTANCES];



static const usbh_classdriver_vmt_t class_driver_vmt = {
	midi_class_init,
	midi_class_load,
	midi_class_unload
};

const usbh_classdriverinfo_t usbhMidiClassDriverInfo = {
	"MIDI", &class_driver_vmt
};

usbh_baseclassdriver_t *midi_class_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem) {
	int i;
	USBHMIDIDriver *midip;
	(void)dev;

	if (_usbh_match_descriptor(descriptor, rem, -1,
			USB_AUDIO_CLASS, USB_MIDISTREAMING_SubCLASS, -1) != HAL_SUCCESS)
		return NULL;

	const usbh_interface_descriptor_t * const ifdesc = (const usbh_interface_descriptor_t *)descriptor;

	if (ifdesc->bNumEndpoints < 1) {
		uerr("MIDI: no endpoints");
		return NULL;
	}

	if (rem == 0) {
		uerr("MIDI: descriptor empty");
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
	midip->nOutputPorts = 0;
	midip->nInputPorts = 0;

	const usbh_ia_descriptor_t *iad = (const usbh_ia_descriptor_t *)descriptor;
	generic_iterator_t iep, icfg, ics;
	if_iterator_t iif;

	uinfof("    Midi descriptor, Length=%d, Hdr=%08x", rem, *(int32_t * )descriptor);
#if 0 // scan the whole configuration, solves issue #408 the wrong way
	cfg_iter_init(&icfg, dev->fullConfigurationDescriptor,
			dev->basicConfigDesc.wTotalLength);
	for (if_iter_init(&iif, &icfg); iif.valid; if_iter_next(&iif)) {
#else // or like in the usbh_custom_class.c
	iif.iad = iad;
	iif.curr = descriptor;
	iif.rem = rem;
	for (ep_iter_init(&iep, &iif); iep.valid; ep_iter_next(&iep)) {
#endif
		const usbh_interface_descriptor_t *const ifdesc = if_get(&iif);
		uinfof("    Midi descriptorx, Hdr=%08x", *(int32_t * )ifdesc);
		if ((ifdesc->bInterfaceClass == USB_AUDIO_CLASS) &&
				(ifdesc->bInterfaceSubClass == USB_MIDISTREAMING_SubCLASS)) {
			for (cs_iter_init(&ics, (generic_iterator_t *)&iif); ics.valid; cs_iter_next(&ics)) {
				switch(ics.curr[2]) {
				case USB_MIDI_SUBTYPE_MS_HEADER: {
					ms_interface_header_descriptor_t *intf_hdr = (ms_interface_header_descriptor_t *)&ics.curr[0];
					uinfof("    Midi interface header, version = %4X",
							intf_hdr->bcdMSC);
				} break;
				case USB_MIDI_SUBTYPE_MIDI_IN_JACK: {
					midi_in_jack_descriptor_t *in_jack_desc = (midi_in_jack_descriptor_t *)&ics.curr[0];
					uinfof("    Midi In jack, bJackType = %d, bJackID = %d, iJack=%d",
							in_jack_desc->bJackType,in_jack_desc->bJackID,in_jack_desc->iJack);
		//			char name[32];
		//			bool res = usbhStdReqGetStringDescriptor(dev, in_jack_desc->iJack, dev->langID0, sizeof(name), (uint8_t *)name);
		//			if (res) {
		//				uinfof("    name %s", name);
		//			} else {
		//				uinfof("    noname");
		//			}
				} break;
				case USB_MIDI_SUBTYPE_MIDI_OUT_JACK: {
					midi_out_jack_descriptor_t *out_jack_desc = (midi_out_jack_descriptor_t *)ics.curr;
					uinfof("    Midi Out jack, bJackType = %d, bJackID = %d, bNrInputPins=%d",
							out_jack_desc->bJackType,out_jack_desc->bJackID,out_jack_desc->bNrInputPins);
				} break;
				default:
					uinfof("    Midi Class-Specific descriptor, Length=%d, Type=%02x",
							ics.curr[0], ics.curr[1]);
					int j;
					for(j=2;j<ics.curr[0];j++)
						uinfof("  %02X", ics.curr[j]);
				}
			}
			// sleep to flush debug output
			chThdSleepMilliseconds(10);

			for (ep_iter_init(&iep, &iif); iep.valid; ep_iter_next(&iep)) {
				const usbh_endpoint_descriptor_t *const epdesc = ep_get(&iep);
				if ((epdesc->bEndpointAddress & 0x80) &&
						((epdesc->bmAttributes == USBH_EPTYPE_BULK) ||
								(epdesc->bmAttributes == USBH_EPTYPE_INT))
								) {
					// some devices use BULK (UC33), some devices use INT endpoints (Launchpad Mini)
					uinfof("IN endpoint found: bEndpointAddress=%02x", epdesc->bEndpointAddress);

					for (cs_iter_init(&ics, &iep); ics.valid; cs_iter_next(&ics)) {
						ms_bulk_data_endpoint_descriptor_t *ms_ep_desc = (ms_bulk_data_endpoint_descriptor_t *)ics.curr;
						if (ms_ep_desc->bDescriptorSubType == USB_MIDI_SUBTYPE_MS_GENERAL) {
							uinfof("    Midi IN endpoint descriptor, bNumEmbMIDIJack=%d",
									ms_ep_desc->bNumEmbMIDIJack);
							int j;
							for(j=0;j<ms_ep_desc->bNumEmbMIDIJack;j++)
								uinfof("    baAssocJackID =  %02X", ms_ep_desc->baAssocJackID[j]);
							midip->nInputPorts = ms_ep_desc->bNumEmbMIDIJack;
						} else {
							uinfof("    Midi IN endpoint descriptor???");
						}
					}

					// Pretend it is an INT IN endpoint to avoid a NAK flood
					usbh_endpoint_descriptor_t *epdesc2 = (usbh_endpoint_descriptor_t *)epdesc;
					epdesc2->bmAttributes |= USBH_EPTYPE_INT;
					usbhEPObjectInit(&midip->epin, dev, epdesc2);
					// but disable FRMOR interrupt, otherwise BULK type EP halts sometimes
//					midip->epin.hcintmsk &= ~HCINTMSK_FRMORM;
//					midip->epin.type = USBH_EPTYPE_INT;
					usbhEPSetName(&midip->epin, "MIDI[IIN ]");
				} else if (((epdesc->bEndpointAddress & 0x80) == 0) &&
					((epdesc->bmAttributes == USBH_EPTYPE_BULK) ||
							(epdesc->bmAttributes == USBH_EPTYPE_INT))
							) {
					// again, some devices use BULK, some devices use INT endpoints
					uinfof("OUT endpoint found: bEndpointAddress=%02x", epdesc->bEndpointAddress);

					for (cs_iter_init(&ics, &iep); ics.valid; cs_iter_next(&ics)) {
						ms_bulk_data_endpoint_descriptor_t *ms_ep_desc = (ms_bulk_data_endpoint_descriptor_t *)ics.curr;
						if (ms_ep_desc->bDescriptorSubType == USB_MIDI_SUBTYPE_MS_GENERAL) {
							uinfof("    Midi OUT endpoint descriptor, bNumEmbMIDIJack=%d",
									ms_ep_desc->bNumEmbMIDIJack);
							int j;
							for(j=0;j<ms_ep_desc->bNumEmbMIDIJack;j++)
								uinfof("    baAssocJackID =  %02X", ms_ep_desc->baAssocJackID[j]);
							midip->nOutputPorts = ms_ep_desc->bNumEmbMIDIJack;
						} else {
							uinfof("    Midi OUT endpoint descriptor???");
						}
					}

					usbhEPObjectInit(&midip->epout, dev, epdesc);
					usbhEPSetName(&midip->epout, "MIDI[IOUT]");
				} else {
					uinfof("unsupported endpoint found: bEndpointAddress=%02x, bmAttributes=%02x",
							epdesc->bEndpointAddress, epdesc->bmAttributes);
				}
				// sleep to flush debug output
				chThdSleepMilliseconds(10);

			}

		} else {
			uwarnf("MIDI: Skipping Interface %d",
					ifdesc->bInterfaceNumber);
		}
	}

	midip->state = USBHMIDI_STATE_ACTIVE;

	return (usbh_baseclassdriver_t *)midip;

}


static void _stop_locked(USBHMIDIDriver *midip) {
	if (midip->state == USBHMIDI_STATE_ACTIVE)
		return;

	osalDbgCheck(midip->state == USBHMIDI_STATE_READY);

	if (midip->epin.status != USBH_EPSTATUS_UNINITIALIZED)
		usbhEPClose(&midip->epin);
	if (midip->epout.status != USBH_EPSTATUS_UNINITIALIZED)
		usbhEPClose(&midip->epout);
	midip->nOutputPorts = 0;
	midip->nInputPorts = 0;
	midip->name[0] = 0;
	midip->state = USBHMIDI_STATE_ACTIVE;
}

void midi_class_unload(usbh_baseclassdriver_t *drv) {
	USBHMIDIDriver *const midip = (USBHMIDIDriver *)drv;
	chSemWait(&midip->sem);
	_stop_locked(midip);
	midip->state = USBHMIDI_STATE_STOP;
	chSemSignal(&midip->sem);
}

static void _object_init(USBHMIDIDriver *midip) {
	osalDbgCheck(midip != NULL);
//	memset(midip, 0, sizeof(*midip));
	midip->info = &usbhMidiClassDriverInfo;
	midip->state = USBHMIDI_STATE_STOP;
	chSemObjectInit(&midip->sem, 1);
}

void midi_class_init(void) {
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
			midip->config->cb_report(midip->config, (uint32_t *)midip->report_buffer, urb->actualLength/4);
		}
		break;
	case USBH_URBSTATUS_TIMEOUT:
		//no data
		break;
	case USBH_URBSTATUS_DISCONNECTED:
		uwarn("MIDI: URB IN disconnected");

		return;
	case USBH_URBSTATUS_ERROR:
		uwarn("MIDI: URB IN error");
		break;
	default:
		uerrf("MIDI: URB IN status unexpected = %d", urb->status);
		break;
	}
	usbhURBObjectResetI(&midip->in_urb);
	usbhURBSubmitI(&midip->in_urb);
}



void usbhmidiStart(USBHMIDIDriver *midip) {
	osalDbgCheck(midip);

	chSemWait(&midip->sem);
	if (midip->state == USBHMIDI_STATE_READY) {
		chSemSignal(&midip->sem);
		return;
	}

	osalDbgCheck(midip->state == USBHMIDI_STATE_ACTIVE);

	usbhDeviceReadString(midip->dev, &midip->name[0], sizeof(midip->name), midip->dev->devDesc.iProduct, midip->dev->langID0);

	if (midip->epout.device != 0) {
		usbhEPOpen(&midip->epout);
	}
	if (midip->epin.device != 0) {
		usbhURBObjectInit(&midip->in_urb, &midip->epin, _in_cb, midip,
                midip->report_buffer, midip->epin.wMaxPacketSize);
		/* open the bulk IN endpoint */
		usbhEPOpen(&midip->epin);

		usbhURBSubmit(&midip->in_urb);
	}

	midip->state = USBHMIDI_STATE_READY;
	chSemSignal(&midip->sem);
}


void usbhmidiStop(USBHMIDIDriver *midip) {
	chSemWait(&midip->sem);
	_stop_locked(midip);
	chSemSignal(&midip->sem);
}

msg_t usbhmidi_sendbuffer(USBHMIDIDriver *midip, uint8_t *buffer, int size) {
	if (midip->state != USBHMIDI_STATE_READY) {
		uinfof("usbhmidi_sendbuffer : device not ready");
		return -1;
	}

	uint32_t actual_len;
	msg_t status = usbhSynchronousTransfer(&midip->epout, buffer,
			size, &actual_len, MS2ST(1000));
	if (status == USBH_URBSTATUS_OK) return MSG_OK;

	return status;
}
