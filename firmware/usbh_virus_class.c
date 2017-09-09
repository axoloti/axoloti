#include "hal.h"

#if !HAL_USE_USBH
#error "USBHMIDI needs USBH"
#endif

#include <string.h>
#include "usbh_midi_class.h"
#include "usbh/internal.h"

#include "midi_buffer.h"
#include "midi_routing.h"
#include "usbh_conf.h"


#if USBH_MIDI_DEBUG_ENABLE_TRACE
#define udbgf(f, ...)  LogTextMessage(f, ##__VA_ARGS__)
#define udbg(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define udbgf(f, ...)  do {} while(0)
#define udbg(f, ...)   do {} while(0)
#endif

#if USBH_MIDI_DEBUG_ENABLE_INFO
#define uinfof(f, ...)  LogTextMessage(f, ##__VA_ARGS__)
#define uinfo(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uinfof(f, ...)  do {} while(0)
#define uinfo(f, ...)   do {} while(0)
#endif

#if USBH_MIDI_DEBUG_ENABLE_WARNINGS
#define uwarnf(f, ...)  LogTextMessage(f, ##__VA_ARGS__)
#define uwarn(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uwarnf(f, ...)  do {} while(0)
#define uwarn(f, ...)   do {} while(0)
#endif

#if USBH_MIDI_DEBUG_ENABLE_ERRORS
#define uerrf(f, ...)  LogTextMessage(f, ##__VA_ARGS__)
#define uerr(f, ...)  usbDbgPuts(f, ##__VA_ARGS__)
#else
#define uerrf(f, ...)  do {} while(0)
#define uerr(f, ...)   do {} while(0)
#endif


#define USB_VENDOR_CLASS_ID 0xFF
#define USB_VENDOR_subCLASS_ID 0x00
#define USB_ACCESS_VID 0x133e
#define USB_VIRUS_PID 0x815


void virus_init(void) {
    // nothing to initialise in the class and midi will do its own things
}

usbh_baseclassdriver_t *virus_load(usbh_device_t *dev, const uint8_t *descriptor, uint16_t rem) {
    if (_usbh_match_descriptor(descriptor, rem, USBH_DT_INTERFACE,
            USB_VENDOR_CLASS_ID, USB_VENDOR_subCLASS_ID, -1) != HAL_SUCCESS) {
        return NULL;
    }


    uinfof("Access Virus detected");

    int i;
    USBHMIDIDriver *midip;
    (void)dev;

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
    midip->nOutputPorts = 0;
    midip->nInputPorts = 0;

    /* parse the configuration descriptor */
    if_iterator_t iif;
    generic_iterator_t iep;
    iif.iad = 0;
    iif.curr = descriptor;
    iif.rem = rem;
    generic_iterator_t ics;

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
//          char name[32];
//          bool res = usbhStdReqGetStringDescriptor(dev, in_jack_desc->iJack, dev->langID0, sizeof(name), (uint8_t *)name);
//          if (res) {
//              uinfof("    name %s", name);
//          } else {
//              uinfof("    noname");
//          }
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

    bool found = false;

    iif.iad = 0;
    iif.curr = descriptor;
    iif.rem = rem;
    for (ep_iter_init(&iep, &iif); iep.valid; ep_iter_next(&iep)) {
        usbh_endpoint_descriptor_t * epdesc = (usbh_endpoint_descriptor_t *)ep_get(&iep);
        if ((epdesc->bEndpointAddress & 0x80) &&
                ((epdesc->bmAttributes == USBH_EPTYPE_BULK) ||
                        (epdesc->bmAttributes == USBH_EPTYPE_INT))
                        ) {
            // some devices use BULK (UC33), some devices use INT endpoints (Launchpad Mini)
            uinfof("IN endpoint found: bEndpointAddress=%02x", epdesc->bEndpointAddress);

            // for (cs_iter_init(&ics, &iep); ics.valid; cs_iter_next(&ics)) {
            //     ms_bulk_data_endpoint_descriptor_t *ms_ep_desc = (ms_bulk_data_endpoint_descriptor_t *)ics.curr;
            //     if (ms_ep_desc->bDescriptorSubType == USB_MIDI_SUBTYPE_MS_GENERAL) {
            //         uinfof("    Midi IN endpoint descriptor, bNumEmbMIDIJack=%d",
            //                 ms_ep_desc->bNumEmbMIDIJack);
            //         int j;
            //         for(j=0;j<ms_ep_desc->bNumEmbMIDIJack;j++)
            //             uinfof("    baAssocJackID =  %02X", ms_ep_desc->baAssocJackID[j]);
            //         midip->nInputPorts = ms_ep_desc->bNumEmbMIDIJack;
            //     } else {
            //         uinfof("    Midi IN endpoint descriptor??? %02x", ms_ep_desc->bDescriptorSubType);
            //     }
            // }


            midip->nInputPorts = epdesc->bEndpointAddress == 0x85;

            // Pretend it is an INT IN endpoint to avoid a NAK flood
            epdesc->bmAttributes |= USBH_EPTYPE_INT;
            usbhEPObjectInit(&midip->epin, dev, epdesc);
            midip->epin.type = USBH_EPTYPE_INT;
            usbhEPSetName(&midip->epin, "MIDI[IIN ]");
        } else if (((epdesc->bEndpointAddress & 0x80) == 0) &&
            ((epdesc->bmAttributes == USBH_EPTYPE_BULK) ||
                    (epdesc->bmAttributes == USBH_EPTYPE_INT))
                    ) {
            // again, some devices use BULK, some devices use INT endpoints
            uinfof("OUT endpoint found: bEndpointAddress=%02x", epdesc->bEndpointAddress);

            // for (cs_iter_init(&ics, &iep); ics.valid; cs_iter_next(&ics)) {
            //     ms_bulk_data_endpoint_descriptor_t *ms_ep_desc = (ms_bulk_data_endpoint_descriptor_t *)ics.curr;
            //     if (ms_ep_desc->bDescriptorSubType == USB_MIDI_SUBTYPE_MS_GENERAL) {
            //         uinfof("    Midi OUT endpoint descriptor, bNumEmbMIDIJack=%d",
            //                 ms_ep_desc->bNumEmbMIDIJack);
            //         int j;
            //         for(j=0;j<ms_ep_desc->bNumEmbMIDIJack;j++)
            //             uinfof("    baAssocJackID =  %02X", ms_ep_desc->baAssocJackID[j]);
            //         midip->nOutputPorts = ms_ep_desc->bNumEmbMIDIJack;
            //     } else {
            //         uinfof("    Midi OUT endpoint descriptor??? %02x", ms_ep_desc->bDescriptorSubType );
            //     }
            // }

            // midip->nOutputPorts =  1;
            // found = true;
            midip->nOutputPorts =  epdesc->bEndpointAddress == 0x05;
            if(epdesc->bEndpointAddress == 0x05)  {
                found = true;
            }

            usbhEPObjectInit(&midip->epout, dev, epdesc);
            usbhEPSetName(&midip->epout, "MIDI[IOUT]");

        } else {
            uinfof("unsupported endpoint found: bEndpointAddress=%02x, bmAttributes=%02x",
                    epdesc->bEndpointAddress, epdesc->bmAttributes);
        }
    }

    if(!found ) return NULL;

    midip->state = USBHMIDI_STATE_ACTIVE;
    uinfof("Switch Virus to USB");

    // doesnt work, seems buffer is initialised after this, so message is never sent
    // if we put this in start, then it does get sent BUT... usbBulkTranser panics
    // so... something is not liked about the virus endpoint!?
    // note: if this is in start, and i connect another device, I get no error
    // so remaining problems are:
    // 1) why does the bulk transfer panic? ... need to check interface description from virus
    // 2) need to trigger this in start, which is called in usbconf.c based on status
    // ....( it maybe we can call start here, as we got the same bulk panic, but thats not the issue)
    midi_message_t m;
    m.bytes.ph = 0x4e;
    m.bytes.b0 = 0x73;
    m.bytes.b1 = 0x52;
    m.bytes.b2 = 0x01;
    midi_output_buffer_t *b = &((USBHMIDIConfig_ext *)midip->config)->out_buffer;
    midi_output_buffer_put(b,m);

    return (usbh_baseclassdriver_t *)midip;
}

void virus_unload(usbh_baseclassdriver_t *drv) {
    midi_class_unload(drv);
}

static const usbh_classdriver_vmt_t virus_class_driver_vmt = {
    virus_init,
    virus_load,
    virus_unload
};

const usbh_classdriverinfo_t usbhVirusClassDriverInfo = {
    "Virus", &virus_class_driver_vmt
};


////VIRUS NOTES:
// USB Device Attached
// PID: 815h
// VID: 133Eh
// Address (#1) assigned.
// cfg desc: num interfaces 5
// interface: interface 0, num 0, numep 0, class 1 , sub class 1
// interface: interface 1, num 1, numep 0, class 1 , sub class 2
// interface: interface 2, num 1, numep 1, class 1 , sub class 2
// endpoint: interface 2, ep num 0, addr  1
// interface: interface 3, num 2, numep 0, class 1 , sub class 2
// interface: interface 4, num 2, numep 1, class 1 , sub class 2
// endpoint: interface 4, ep num 0, addr  82
// interface: interface 5, num 3, numep 2, class FF , sub class 0
// interface: more interfaces described, that config detailed, use actual number
// endpoint: interface 5, ep num 0, addr  5
// endpoint: interface 5, ep num 1, addr  85
// Manufacturer : Access Music
// Product : Virus TI
// Serial Number : N/A
// Enumeration done.
// This device has only 1 configuration.
// Default configuration set.
// USB Access Virus detected
// Switching to Interface (#5)
// Class    : FFh
// SubClass : 0h
// Protocol : 0h
// USB Host (Virus) Output size requests : 8
// USB Host (Virus) Input size requests : 10
// USB Host (Virus) Output connected to 5 : 5
// Switch Virus to USB mode
// USB Host (Virus) Input connected to 5 : 85

