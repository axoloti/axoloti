/*
 ChibiOS/RT - Copyright (C) 2006-2013 Giovanni Di Sirio

 modified by Johannes Taelman

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

#include "ch.h"
#include "hal.h"
#include "usbcfg.h"


/*
 * Serial over USB Driver structure.
 */
MidiUSBDriver MDU1;
BulkUSBDriver BDU1;

/*
 * USB Device Descriptor.
 */
static const uint8_t vcom_device_descriptor_data[18] = {
  USB_DESC_DEVICE       (0x0200,        /* bcdUSB (2.0).                    */
                         0xEF,          /* bDeviceClass (Misc).             */
                         0x02,          /* bDeviceSubClass.                 */
                         0x01,          /* bDeviceProtocol. (IAD)           */
                         0x40,          /* bMaxPacketSize.                  */
                         0x16C0,        /* idVendor (Voti).                 */
                         0x0442,        /* idProduct.                       */
                         0x0200,        /* bcdDevice.                       */
                         1,             /* iManufacturer.                   */
                         5,             /* iProduct.                        */
                         3,             /* iSerialNumber.                   */
                         1)             /* bNumConfigurations.              */
};

/*
 * Device Descriptor wrapper.
 */
static const USBDescriptor vcom_device_descriptor = {
  sizeof vcom_device_descriptor_data,
  vcom_device_descriptor_data
};

static const uint8_t vcom_configuration_descriptor_data[]=
{
 /* Configuration Descriptor.*/
 USB_DESC_CONFIGURATION(140,            /* wTotalLength.                    */
                        0x03,          /* bNumInterfaces.                  */
                        0x01,          /* bConfigurationValue.             */
                        5,             /* iConfiguration.                  */
                        0xC0,          /* bmAttributes (self powered).     */
                        50),           /* bMaxPower (100mA).               */
 /* Interface Association Descriptor.*/
 USB_DESC_INTERFACE_ASSOCIATION(0x00, /* bFirstInterface.                  */
                                0x02, /* bInterfaceCount.                  */
                                0x01, /* bFunctionClass (Audio).           */
                                0x03, /* bFunctionSubClass.                */
                                0x00, /* bFunctionProcotol                 */
                                0),   /* iInterface.                       */
  0x09, 0x04, 0x00, 0x00, 0x00, 0x01, 0x01, 0x00, 0x00, // Interface 0              INTERFACE DESC (bLength bDescType bInterfaceNumber bAltSetting bNumEndpoints bInterfaceClass bInterfaceSubClass bInterfaceProtocol iInterface)
  0x09, 0x24, 0x01, 0x00, 0x01, 0x09, 0x00, 0x01, 0x01, // CS Interface (audio)     CLASS SPECIFIC AC INTERFACE DESC
  0x09, 0x04, 0x01, 0x00, 0x02, 0x01, 0x03, 0x00, 0x00, // Interface 1              INTERFACE DESC (bLength bDescType bInterfaceNumber bAltSetting bNumEndpoints bInterfaceClass bInterfaceSubClass bInterfaceProtocol iInterface)
  0x07, 0x24, 0x01, 0x00, 0x01, 0x41, 0x00,             // CS Interface (midi)      CLASS SPECIFIC MS INTERFACE DESC
  0x06, 0x24, 0x02, 0x01, 0x01, 0x05,                   //   IN  Jack 1 (emb)       MIDI IN JACK DESC (bLength bDescType bDescSubType bJackType bJackID iJack)
  0x06, 0x24, 0x02, 0x02, 0x02, 0x06,                   //   IN  Jack 2 (ext)       MIDI IN JACK DESC (bLength bDescType bDescSubType bJackType bJackID iJack)
  0x09, 0x24, 0x03, 0x01, 0x03, 0x01, 0x02, 0x01, 0x06, //   OUT Jack 3 (emb)       MIDI OUT JACK DESC (bLength bDescType bDescSubType bJackType bJackID bNrInputPins baSourceID(1) baSourceID(1) iJack)
  0x09, 0x24, 0x03, 0x02, 0x04, 0x01, 0x01, 0x01, 0x02, //   OUT Jack 4 (ext)       MIDI OUT JACK DESC (bLength bDescType bDescSubType bJackType bJackID bNrInputPins baSourceID(1) baSourceID(1) iJack)
  0x09, 0x05, 0x01, 0x02, 0x40, 0x00, 0x00, 0x00, 0x00, // Endpoint OUT             ENDPOINT DESC  (bLength bDescType bEndpointAddr bmAttr wMaxPacketSize(2 bytes)  bInterval bRefresh bSyncAddress)
  0x05, 0x25, 0x01, 0x01, 0x01,                         //   CS EP IN  Jack         CLASS SPECIFIC MS BULK DATA EP DESC
  0x09, 0x05, 0x81, 0x02, 0x40, 0x00, 0x00, 0x00, 0x00, // Endpoint IN              ENDPOINT DESC  (bLength bDescType bEndpointAddr bmAttr wMaxPacketSize(2 bytes)  bInterval bRefresh bSyncAddress)
  0x05, 0x25, 0x01, 0x01, 0x03,                         //   CS EP OUT Jack          CLASS SPECIFIC MS BULK DATA EP DESC

  /* Interface Association Descriptor.*/
  USB_DESC_INTERFACE_ASSOCIATION(0x02, /* bFirstInterface.                  */
                              0x01, /* bInterfaceCount.                  */
                              0xFF, /* bFunctionClass (Vendor Specific).  */
                              0x00, /* bFunctionSubClass.                */
                              0x00, /* bFunctionProcotol                 */
                              4),   /* iInterface.                       */
  /* Interface Descriptor.*/
  USB_DESC_INTERFACE    (0x02,          /* bInterfaceNumber.                */
                         0x00,          /* bAlternateSetting.               */
                         0x02,          /* bNumEndpoints.                   */
                         0xFF,          /* bInterfaceClass (Vendor Specific). */
                         0x00,
                         0x00,
                         4),         /* iInterface.                      */
  /* Endpoint 2 Descriptor.*/
  USB_DESC_ENDPOINT     (USBD2_DATA_AVAILABLE_EP,       /* bEndpointAddress.*/
                         0x02,          /* bmAttributes (Bulk).             */
                         0x0040,        /* wMaxPacketSize.                  */
                         0x00),         /* bInterval.                       */
  /* Endpoint 2 Descriptor.*/
  USB_DESC_ENDPOINT     (USBD2_DATA_REQUEST_EP|0x80,    /* bEndpointAddress.*/
                         0x02,          /* bmAttributes (Bulk).             */
                         0x0040,        /* wMaxPacketSize.                  */
                         0x00)          /* bInterval.                       */

};


static const USBDescriptor vcom_configuration_descriptor = {
  sizeof vcom_configuration_descriptor_data,
  vcom_configuration_descriptor_data
};

/*
 * U.S. English language identifier.
 */
static const uint8_t vcom_string0[] = {
  USB_DESC_BYTE(4),                     /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  USB_DESC_WORD(0x0409)                 /* wLANGID (U.S. English).          */
};

/*
 * Vendor string.
 */
static const uint8_t vcom_string1[] = {
  USB_DESC_BYTE(16),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  'A', 0, 'x', 0, 'o', 0, 'l', 0, 'o', 0, 't', 0, 'i', 0
};

/*
 * Device Description string.
 */
static const uint8_t vcom_string2[] = {
  USB_DESC_BYTE(56),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  'C', 0, 'h', 0, 'i', 0, 'b', 0, 'i', 0, 'O', 0, 'S', 0, '/', 0,
  'R', 0, 'T', 0, ' ', 0, 'V', 0, 'i', 0, 'r', 0, 't', 0, 'u', 0,
  'a', 0, 'l', 0, ' ', 0, 'C', 0, 'O', 0, 'M', 0, ' ', 0, 'P', 0,
  'o', 0, 'r', 0, 't', 0
};

/*
 * Serial Number string.
 */
static const uint8_t vcom_string3[] = {
  USB_DESC_BYTE(8),                     /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  '0' + CH_KERNEL_MAJOR, 0,
  '0' + CH_KERNEL_MINOR, 0,
  '0' + CH_KERNEL_PATCH, 0
};

static uint8_t descriptor_serial_string[] = {
  USB_DESC_BYTE(50),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0,
  '0', 0, '0', 0, '0', 0, '0', 0
};

static const USBDescriptor descriptor_serial = {
   sizeof descriptor_serial_string, descriptor_serial_string,
};

/*
 * Device Description string.
 */
static const uint8_t vcom_string4[] = {
  USB_DESC_BYTE(46),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  'A', 0, 'x', 0, 'o', 0, 'l', 0, 'o', 0, 't', 0, 'i', 0, ' ', 0,
  'B', 0, 'u', 0, 'l', 0, 'k', 0, ' ', 0,
  'I', 0, 'n', 0, 't', 0, 'e', 0, 'r', 0, 'f', 0, 'a', 0, 'c', 0,
  'e', 0
};

/*
 * Device Description string.
 */
static const uint8_t vcom_string5[] = {
  USB_DESC_BYTE(26),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  'A', 0, 'x', 0, 'o', 0, 'l', 0, 'o', 0, 't', 0, 'i', 0, ' ', 0,
  'C', 0, 'o', 0, 'r', 0, 'e', 0
};



/*
 * Device Description string.
 */
static const uint8_t vcom_string6[] = {
  USB_DESC_BYTE(26),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  'A', 0, 'x', 0, 'o', 0, 'l', 0, 'o', 0, 't', 0, 'i', 0, ' ', 0,
  'M', 0, 'I', 0, 'D', 0, 'I', 0
};



/* WCID implementation reference:
 *  https://github.com/pbatard/libwdi/wiki/WCID-Devices
 */
static const uint8_t vcid_string[] = {
  USB_DESC_BYTE(18),                    /* bLength.                         */
  USB_DESC_BYTE(USB_DESCRIPTOR_STRING), /* bDescriptorType.                 */
  'M', 0, 'S', 0, 'F', 0, 'T', 0, '1', 0, '0', 0, '0', 0, /* MSFT100        */
  20, 0x00 /* vendor code, padding */
};

/*
 * Strings wrappers array.
 */
static const USBDescriptor vcom_strings[] = {
  {sizeof vcom_string0, vcom_string0},
  {sizeof vcom_string1, vcom_string1},
  {sizeof vcom_string2, vcom_string2},
  {sizeof vcom_string3, vcom_string3},
  {sizeof vcom_string4, vcom_string4},
  {sizeof vcom_string5, vcom_string5},
  {sizeof vcom_string6, vcom_string6}
};

static const USBDescriptor vcid_descriptor = {sizeof vcid_string, vcid_string};

void inttohex(uint32_t v, unsigned char *p){
  int nibble;
  for (nibble = 0;nibble<8;nibble++){
    unsigned char c = (v>>(28-nibble*4))&0xF;
    if (c<10) c=c+'0';
    else c=c+'A'-10;
    *p = c;
    p += 2;
  }
}


/*
 * Handles the GET_DESCRIPTOR callback. All required descriptors must be
 * handled here.
 */
static const USBDescriptor *get_descriptor(USBDriver *usbp,
                                           uint8_t dtype,
                                           uint8_t dindex,
                                           uint16_t lang) {

  (void)usbp;
  (void)lang;
  switch (dtype) {
  case USB_DESCRIPTOR_DEVICE:
    return &vcom_device_descriptor;
  case USB_DESCRIPTOR_CONFIGURATION:
    return &vcom_configuration_descriptor;
  case USB_DESCRIPTOR_STRING:
    if (dindex == 3) {
      inttohex(*((uint32_t*)0x1FFF7A10),&descriptor_serial_string[2]);
      inttohex(*((uint32_t*)0x1FFF7A14),&descriptor_serial_string[2+16]);
      inttohex(*((uint32_t*)0x1FFF7A18),&descriptor_serial_string[2+32]);
      return &descriptor_serial;
    }
    if (dindex < 9)
      return &vcom_strings[dindex];
  case 0xEE:
    return &vcid_descriptor;
  }
  return NULL;
}

/**
 * @brief   IN EP1 state.
 */
static USBInEndpointState ep1instate;

/**
 * @brief   OUT EP1 state.
 */
static USBOutEndpointState ep1outstate;

/**
 * @brief   EP1 initialization structure (both IN and OUT).
 */
static const USBEndpointConfig ep1config = {
  USB_EP_MODE_TYPE_BULK,
  NULL,
  mduDataTransmitted,
  mduDataReceived,
  0x0040,
  0x0040,
  &ep1instate,
  &ep1outstate,
  1,
  NULL
};

/**
 * @brief   IN EP2 state.
 */
static USBInEndpointState ep2instate;

/**
 * @brief   OUT EP2 state.
 */
static USBOutEndpointState ep2outstate;

/**
 * @brief   EP2 initialization structure (both IN and OUT).
 */
static const USBEndpointConfig ep2config = {
  USB_EP_MODE_TYPE_BULK,
  NULL,
  bduDataTransmitted,
  bduDataReceived,
  0x0040,
  0x0040,
  &ep2instate,
  &ep2outstate,
  1,
  NULL
};

/*
 * Handles the USB driver global events.
 */
static void usb_event(USBDriver *usbp, usbevent_t event) {

  switch (event) {
  case USB_EVENT_RESET:
    return;
  case USB_EVENT_ADDRESS:
    return;
  case USB_EVENT_CONFIGURED:
    chSysLockFromIsr();

    /* Enables the endpoints specified into the configuration.
       Note, this callback is invoked from an ISR so I-Class functions
       must be used.*/
    usbInitEndpointI(usbp, USBD1_DATA_REQUEST_EP, &ep1config);
    usbInitEndpointI(usbp, USBD2_DATA_REQUEST_EP, &ep2config);

    /* Resetting the state of the Bulk driver subsystem.*/
    bduConfigureHookI(&BDU1);
    mduConfigureHookI(&MDU1);

    chSysUnlockFromIsr();
    return;
  case USB_EVENT_SUSPEND:
    return;
  case USB_EVENT_WAKEUP:
    return;
  case USB_EVENT_STALLED:
    return;
  }
  return;
}

static const uint8_t mscompatid[] = {
0x40, 0x00, 0x00, 0x00,  /* DWORD (LE)  Descriptor length (64 bytes) */
0x00, 0x01,  /* BCD WORD (LE)   Version ('1.0') */
0x04, 0x00,  /* WORD (LE)   Compatibility ID Descriptor index (0x0004) */
0x02,    /* BYTE    Number of sections (2) */
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,    /* 7 BYTES     Reserved */
0x00,    /* BYTE    Interface Number (Interface #0) */
0x01,    /* BYTE    Reserved */
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  /* 8 BYTES (NUL-terminated?) ASCII String    Compatible ID (null) */
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  /* 8 BYTES (NUL-terminated?) ASCII String    Sub-Compatible ID (unused) */
0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  /* 6 BYTES      Reserved */
0x02,    /* BYTE    Interface Number (Interface #2) */
0x01,    /* BYTE    Reserved */
0x57, 0x49, 0x4E, 0x55, 0x53, 0x42, 0x00, 0x00,  /* 8 BYTES (NUL-terminated?) ASCII String    Compatible ID ("WINUSB\0\0") */
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  /* 8 BYTES (NUL-terminated?) ASCII String    Sub-Compatible ID (unused) */
0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  /* 6 BYTES      Reserved */
};


static const uint8_t msdescriptor0[] = {
0x0A, 0x00, 0x00, 0x00,  /* Descriptor length (10 bytes) */
0x00, 0x01,  /*Version ('1.0') */
0x05, 0x00, /*  WORD (LE)   Descriptor index (5) */
0x00, 0x00  /*  WORD (LE)   Number of sections (0) */
};

static const uint8_t msdescriptor1[] = {
0x94, 0x00, 0x00, 0x00,  /* Descriptor length (146 bytes) */
0x00, 0x01,  /*Version ('1.0') */
0x05, 0x00, /*  WORD (LE)   Descriptor index (5) */
0x01, 0x00, /*  WORD (LE)   Number of sections (1) */
0x8A, 0x00, 0x00, 0x00,  /* DWORD (LE)  Size of the property section (136 bytes) */
0x07, 0x00, 0x00, 0x00,  /* DWORD (LE)  Property data type (7 = Unicode REG_MULTI_SZ, */
0x2a, 0x00, /*  WORD (LE)   Property name length (42 bytes) */
0x44, 0x00, 0x65, 0x00, 0x76, 0x00, 0x69, 0x00,
0x63, 0x00, 0x65, 0x00, 0x49, 0x00, 0x6E, 0x00,
0x74, 0x00, 0x65, 0x00, 0x72, 0x00, 0x66, 0x00,
0x61, 0x00, 0x63, 0x00, 0x65, 0x00, 0x47, 0x00,
0x55, 0x00, 0x49, 0x00, 0x44, 0x00, 0x73, 0x00, 0x00, 0x00, /* NUL-terminated Unicode String (LE) Property name "DeviceInterfaceGUIDs" */
0x50, 0x00, 0x00, 0x00,  /* DWORD (LE)  Property data length (80 bytes) */
0x7B, 0x00, 0x38, 0x00, 0x38, 0x00, 0x42, 0x00,
0x41, 0x00, 0x45, 0x00, 0x30, 0x00, 0x33, 0x00,
0x32, 0x00, 0x2D, 0x00, 0x35, 0x00, 0x41, 0x00,
0x38, 0x00, 0x31, 0x00, 0x2D, 0x00, 0x34, 0x00,
0x39, 0x00, 0x66, 0x00, 0x30, 0x00, 0x2D, 0x00,
0x42, 0x00, 0x43, 0x00, 0x33, 0x00, 0x44, 0x00,
0x2D, 0x00, 0x41, 0x00, 0x34, 0x00, 0x46, 0x00,
0x46, 0x00, 0x31, 0x00, 0x33, 0x00, 0x38, 0x00,
0x32, 0x00, 0x31, 0x00, 0x36, 0x00, 0x44, 0x00,
0x36, 0x00, 0x7D, 0x00, 0x00, 0x00, 0x00, 0x00
/* double NUL-terminated Unicode String (LE) Property name "{88BAE032-5A81-49f0-BC3D-A4FF138216D6}" */
};

static bool_t specialRequestsHook(USBDriver *usbp) {
  if (
      (usbp->setup[0] == 0xC0) &&
      (usbp->setup[1] == 0x14) &&
      (usbp->setup[2] == 0x00) &&
      (usbp->setup[3] == 0x00) &&
      (usbp->setup[4] == 0x04)
    ) {
    usbSetupTransfer(usbp, (uint8_t *)&mscompatid, usbp->setup[6], NULL);
    return TRUE;
  } else   if (
      (usbp->setup[0] == 0xC1) &&
      (usbp->setup[1] == 0x14) &&
      (usbp->setup[2] == 0x00) &&
      (usbp->setup[3] == 0x00) &&
      (usbp->setup[4] == 0x05)
    ) {
    usbSetupTransfer(usbp, (uint8_t *)&msdescriptor0, usbp->setup[6], NULL);
    return TRUE;
  } else   if (
      (usbp->setup[0] == 0xC1) &&
      (usbp->setup[1] == 0x14) &&
      (usbp->setup[2] == 0x02) &&
      (usbp->setup[3] == 0x00) &&
      (usbp->setup[4] == 0x05)
    ) {
    usbSetupTransfer(usbp, (uint8_t *)&msdescriptor1, usbp->setup[6], NULL);
    return TRUE;
  }

  return FALSE;
}

/*
 * USB driver configuration.
 */
const USBConfig usbcfg = {
  usb_event,
  get_descriptor,
  specialRequestsHook,
  NULL
};

/*
 * Midi USB driver configuration.
 */
const MidiUSBConfig midiusbcfg = {
  &USBD1,
  USBD1_DATA_REQUEST_EP,
  USBD1_DATA_AVAILABLE_EP
};

/*
 * Bulk USB driver configuration.
 */
const BulkUSBConfig bulkusbcfg = {
  &USBD1,
  USBD2_DATA_REQUEST_EP,
  USBD2_DATA_AVAILABLE_EP
};

