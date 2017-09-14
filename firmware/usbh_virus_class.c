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

void LogTextMessage(const char* format, ...);

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
    int i;
    USBHMIDIDriver *midip;
    (void)dev;

    const usbh_interface_descriptor_t * const ifdesc = (const usbh_interface_descriptor_t *)descriptor;

    // we are only interested in interface 3, endpoint 5 and 85
    if (       ifdesc->bInterfaceNumber != 3
            || (ifdesc->bAlternateSetting != 0)
            || (ifdesc->bNumEndpoints < 1)) {
        return NULL;
    }


    uinfof("Access Virus detected");

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
    // usbhStdReqSetInterface(dev,midip->ifnum,0);
    midip->nOutputPorts = 0;
    midip->nInputPorts = 0;


    // based on usbh_midi_class
    // but we can remove the logging and checking of config, since we know what the virus has available.
    bool found = false;

    generic_iterator_t iep, icfg, ics;
    if_iterator_t iif;

    // generic_iterator_t iep;
    iif.iad = (const usbh_ia_descriptor_t *)descriptor;
    iif.curr = descriptor;
    iif.rem = rem;
    for (ep_iter_init(&iep, &iif); iep.valid; ep_iter_next(&iep)) {
        usbh_endpoint_descriptor_t * epdesc = (usbh_endpoint_descriptor_t *)ep_get(&iep);
        if ((epdesc->bEndpointAddress & 0x80) &&
                ((epdesc->bmAttributes == USBH_EPTYPE_BULK) ||
                        (epdesc->bmAttributes == USBH_EPTYPE_INT))
                        ) {
            // some devices use BULK (UC33), some devices use INT endpoints (Launchpad Mini)
            uinfof("IN endpoint found: interface %02x bEndpointAddress=%02x", midip->ifnum, epdesc->bEndpointAddress);

            if(epdesc->bEndpointAddress == 0x85)  {
                uinfof("Virus input found %02x", epdesc->bEndpointAddress);
                midip->nInputPorts = 2;
                // Pretend it is an INT IN endpoint to avoid a NAK flood
                usbh_endpoint_descriptor_t *epdesc2 = (usbh_endpoint_descriptor_t *)epdesc;
                epdesc2->bmAttributes |= USBH_EPTYPE_INT;
                usbhEPObjectInit(&midip->epin, dev, epdesc2);
                // but disable FRMOR interrupt, otherwise BULK type EP halts sometimes
//                  midip->epin.hcintmsk &= ~HCINTMSK_FRMORM;
//                  midip->epin.type = USBH_EPTYPE_INT;
                usbhEPSetName(&midip->epin, "MIDI[IIN ]");
            }
        } else if (((epdesc->bEndpointAddress & 0x80) == 0) &&
            ((epdesc->bmAttributes == USBH_EPTYPE_BULK) ||
                    (epdesc->bmAttributes == USBH_EPTYPE_INT))
                    ) {
            // again, some devices use BULK, some devices use INT endpoints
            uinfof("OUT endpoint found: interface %02x bEndpointAddress=%02x", midip->ifnum, epdesc->bEndpointAddress);

            if(epdesc->bEndpointAddress == 0x05)  {
                uinfof("Virus output found %02x", epdesc->bEndpointAddress);
                midip->nOutputPorts =  2;
                found = true;

                usbhEPObjectInit(&midip->epout, dev, epdesc);
                usbhEPSetName(&midip->epout, "MIDI[IOUT]");
            }


        } else {
            uinfof("unsupported endpoint found: bEndpointAddress=%02x, bmAttributes=%02x",
                    epdesc->bEndpointAddress, epdesc->bmAttributes);
        }
    }

    if(!found ) return NULL;

    midip->state = USBHMIDI_STATE_ACTIVE;



    // usbhmidiStart , opens the endpoints needed to send data 
    // this is usually called in usbh_conf... but seem ok here, 
    // this  sets state to READY, so usb_conf wont do it again
    usbhmidiStart(midip);

    //usbhStart:usbhDeviceReadString not working, so force name for now
    strcpy(midip->name,"Virus");
    // now we have to send the magic seq to switch virus to midi usb mode
    LogTextMessage("Virus : Connected");

    // additional start code usually done in usbhconf
    USBHMIDIConfig_ext* config = (USBHMIDIConfig_ext *)midip->config;
    config->in_mapping->name = midip->name;
    config->in_mapping->nports = midip->nInputPorts;
    config->out_mapping->name = midip->name;
    config->out_mapping->nports = midip->nOutputPorts;

    // now try to sent the data
    uinfof("Switch Virus to USB");
    static uint8_t seq[] = { 0x4e, 0x73, 0x52, 0x01 }; 

    usbhmidi_sendbuffer(midip,seq,sizeof(seq));

    return (usbh_baseclassdriver_t *) midip;
}

void virus_unload(usbh_baseclassdriver_t *drv) {
    uinfof("Virus unloading");
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

