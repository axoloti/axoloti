#include "usb_msd.h"
#include "ch.h"
#include "hal.h"
#include <stdio.h>
#include <string.h>
#include "axoloti_board.h"

/* endpoint index */
#define USB_MS_DATA_EP 1

/* USB device descriptor */
static const uint8_t deviceDescriptorData[] =
{
    USB_DESC_DEVICE
    (
        0x0200, /* supported USB version (2.0)                     */
        0x00,   /* device class (none, specified in interface)     */
        0x00,   /* device sub-class (none, specified in interface) */
        0x00,   /* device protocol (none, specified in interface)  */
        64,     /* max packet size of control end-point            */
        0x16C0, /* vendor ID (Voti)                                */
        0x0443, /* product ID (lab use only!)                      */
        0x0100, /* device release number                           */
        1,      /* index of manufacturer string descriptor         */
        2,      /* index of product string descriptor              */
        3,      /* index of serial number string descriptor        */
        1       /* number of possible configurations               */
    )
};
static const USBDescriptor deviceDescriptor =
{
    sizeof(deviceDescriptorData),
    deviceDescriptorData
};

/* configuration descriptor */
static const uint8_t configurationDescriptorData[] =
{
    /* configuration descriptor */
    USB_DESC_CONFIGURATION
    (
        32,   /* total length                                             */
        1,    /* number of interfaces                                     */
        1,    /* value that selects this configuration                    */
        0,    /* index of string descriptor describing this configuration */
        0xC0, /* attributes (self-powered)                                */
        50    /* max power (100 mA)                                       */
    ),

    /* interface descriptor */
    USB_DESC_INTERFACE
    (
        0,    /* interface number                                     */
        0,    /* value used to select alternative setting             */
        2,    /* number of end-points used by this interface          */
        0x08, /* interface class (Mass Storage)                       */
        0x06, /* interface sub-class (SCSI Transparent Storage)       */
        0x50, /* interface protocol (Bulk Only)                       */
        0     /* index of string descriptor describing this interface */
    ),

    /* end-point descriptor */
    USB_DESC_ENDPOINT
    (
        USB_MS_DATA_EP | 0x00, /* address (end point index | IN direction)       */
        USB_EP_MODE_TYPE_BULK, /* attributes (bulk)                              */
        64,                    /* max packet size                                */
        0x05                   /* polling interval (ignored for bulk end-points) */
    ),

    /* end-point descriptor */
    USB_DESC_ENDPOINT
    (
        USB_MS_DATA_EP | 0x80, /* address (end point index | OUT direction)      */
        USB_EP_MODE_TYPE_BULK, /* attributes (bulk)                              */
        64,                    /* max packet size                                */
        0x05                   /* polling interval (ignored for bulk end-points) */
    )
};
static const USBDescriptor configurationDescriptor =
{
    sizeof(configurationDescriptorData),
    configurationDescriptorData
};

/* Language descriptor */
static const uint8_t languageDescriptorData[] =
{
    USB_DESC_BYTE(4),
    USB_DESC_BYTE(USB_DESCRIPTOR_STRING),
    USB_DESC_WORD(0x0409) /* U.S. english */
};
static const USBDescriptor languageDescriptor =
{
    sizeof(languageDescriptorData),
    languageDescriptorData
};

/* Vendor descriptor */
static const uint8_t vendorDescriptorData[] =
{
    USB_DESC_BYTE(16),
    USB_DESC_BYTE(USB_DESCRIPTOR_STRING),
    'A', 0, 'x', 0, 'o', 0, 'l', 0, 'o', 0, 't', 0, 'i', 0
};
static const USBDescriptor vendorDescriptor =
{
    sizeof(vendorDescriptorData),
    vendorDescriptorData
};

/* Product descriptor */
static const uint8_t productDescriptorData[] =
{
    USB_DESC_BYTE(22),
    USB_DESC_BYTE(USB_DESCRIPTOR_STRING),
    'C', 0, 'a', 0, 'r', 0, 'd', 0, 'r', 0, 'e', 0, 'a', 0, 'd', 0, 'e', 0, 'r', 0
};
static const USBDescriptor productDescriptor =
{
    sizeof(productDescriptorData),
    productDescriptorData
};

/* Serial number descriptor */
static const uint8_t serialNumberDescriptorData[] =
{
    USB_DESC_BYTE(26),
    USB_DESC_BYTE(USB_DESCRIPTOR_STRING),
    '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '0', 0, '1', 0
};
static const USBDescriptor serialNumberDescriptor =
{
    sizeof(serialNumberDescriptorData),
    serialNumberDescriptorData
};

/* Handles GET_DESCRIPTOR requests from the USB host */
static const USBDescriptor* getDescriptor(USBDriver* usbp, uint8_t type, uint8_t index, uint16_t lang)
{
    (void)usbp;
    (void)lang;

    switch (type)
    {
        case USB_DESCRIPTOR_DEVICE:
            return &deviceDescriptor;

        case USB_DESCRIPTOR_CONFIGURATION:
            return &configurationDescriptor;

        case USB_DESCRIPTOR_STRING:
            switch (index)
            {
                case 0: return &languageDescriptor;
                case 1: return &vendorDescriptor;
                case 2: return &productDescriptor;
                case 3: return &serialNumberDescriptor;
            }
    }

    return 0;
}



/* USB mass storage driver */
USBMassStorageDriver UMSD1;


/* Handles global events of the USB driver */
static void usbEvent(USBDriver* usbp, usbevent_t event)
{
    switch (event)
    {
        case USB_EVENT_CONFIGURED:
            chSysLockFromIsr();
//            usbInitEndpointI(usbp, USB_MS_DATA_EP, &ep_data_config);
            msdConfigureHookI(&UMSD1);
            chSysUnlockFromIsr();
            break;

        case USB_EVENT_RESET:
        case USB_EVENT_ADDRESS:
        case USB_EVENT_SUSPEND:
        case USB_EVENT_WAKEUP:
        case USB_EVENT_STALLED:
        default:
            break;
    }
}

/* Configuration of the USB driver */
static const USBConfig usbConfig =
{
    usbEvent,
    getDescriptor,
    msdRequestsHook,
    0
};

/* Turns on a LED when there is I/O activity on the USB port */
static void usbActivity(bool_t active)
{
    if (active)
        palSetPad(LED1_PORT, LED1_PIN);
    else
        palClearPad(LED1_PORT, LED1_PIN);
}

/* USB mass storage configuration */
static const USBMassStorageConfig msdConfig =
{
    &USBD1,
    (BaseBlockDevice*)&SDCD1,
    USB_MS_DATA_EP,
    &usbActivity,
    "Axoloti",
    "Cardreader",
    "0.1"
};

int main(void)
{
    /* system & hardware initialization */
    halInit();

    // float usb inputs, hope the host notices detach...
    palSetPadMode(GPIOA, 11, PAL_MODE_INPUT);
    palSetPadMode(GPIOA, 12, PAL_MODE_INPUT);
    // setup LEDs, red+green on
    palSetPadMode(LED1_PORT, LED1_PIN, PAL_MODE_OUTPUT_PUSHPULL);
    palSetPadMode(LED2_PORT, LED2_PIN, PAL_MODE_OUTPUT_PUSHPULL);
    palClearPad(LED1_PORT,LED1_PIN);
    palClearPad(LED2_PORT,LED2_PIN);

    chSysInit();

    palSetPadMode(GPIOA, 11, PAL_MODE_ALTERNATE(10));
    palSetPadMode(GPIOA, 12, PAL_MODE_ALTERNATE(10));

    palSetPadMode(GPIOC, 8, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 9, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 10, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 11, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 12, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOD, 2, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    chThdSleepMilliseconds(50);

    /* initialize the SD card */
    sdcStart(&SDCD1, NULL);
    sdcConnect(&SDCD1);

    /* initialize the USB mass storage driver */
    msdInit(&UMSD1);

    /* turn off green LED, turn on red LED */
    palClearPad(LED1_PORT, LED1_PIN);
    palSetPad(LED2_PORT, LED2_PIN);

    /* start the USB mass storage service */
    int ret = msdStart(&UMSD1, &msdConfig);
    if (ret != 0) {
        /* no media found : bye bye !*/
        usbDisconnectBus(&USBD1);
        chThdSleepMilliseconds(1000);
        NVIC_SystemReset();
    }

    /* watch the mass storage events */
    EventListener connected;
    EventListener ejected;
    chEvtRegisterMask(&UMSD1.evt_connected, &connected, EVENT_MASK(1));
    chEvtRegisterMask(&UMSD1.evt_ejected, &ejected, EVENT_MASK(2));


    /* start the USB driver */
    usbDisconnectBus(&USBD1);
    chThdSleepMilliseconds(1000);
    usbStart(&USBD1, &usbConfig);
    usbConnectBus(&USBD1);


    while (TRUE)
    {
        eventmask_t event = chEvtWaitOne(EVENT_MASK(1) | EVENT_MASK(2));
        if (event == EVENT_MASK(1))
        {
            /* media connected */
        }
        else if (event == EVENT_MASK(2))
        {
            /* media ejected : bye bye !*/
            usbDisconnectBus(&USBD1);
            chThdSleepMilliseconds(1000);
            NVIC_SystemReset();
        }
    }

    return 0;
}
