#include "ch.h"
#include "hal.h"
#include "usbh_midi_class.h"
#include "midi_buffer.h"


/* notes:
 * * debugging is active on SD2
 *   (ENABLE_SERIAL_DEBUG)
 * * midi output is not implemented yet
 * * compiled with ChibiOS-Contrib
 *   from https://github.com/dismirlian/ChibiOS-Contrib/commit/2e2e10417e4e2a6e2aec3493fd65859ad80e8c30
*/

char usbh_midi_in_buf[64];

void usbhmidi_cb(USBHMIDIDriver *midip, uint16_t len) {
	int i;
	char *buf = midip->config->report_buffer;
	for (i = 0; i < len; i += 4) {
		if (buf[i]) {
			midi_message_t m;
			m.word = *(uint32_t *) &buf[i];
			usbh_midi_dispatch(m);
			//usbDbgPuts("cb!");
		}
	}
}

USBHMIDIConfig midiconf = { usbhmidi_cb, usbh_midi_in_buf, 64 };

static void ThreadTestMIDI(void *p) {
	(void) p;
	USBHMIDIDriver * const midipp = &USBHMIDID[0];

	start: while (midipp->state != USBHMIDI_STATE_ACTIVE) {
		chThdSleepMilliseconds(100);
	}

	usbDbgPuts("MIDI: Connected");

	usbhmidiStart(midipp, &midiconf);

	while (1) { // fixme: handle disconnect
		// midipp->state == USBHMIDI_STATE_ACTIVE) {
		chThdSleepMilliseconds(100);
	}

	usbhmidiStop(midipp, &midiconf);

	usbDbgPuts("MIDI: restarting in 3s");
	chThdSleepMilliseconds(3000);

	goto start;
}

static THD_WORKING_AREA(waTestMIDI, 1024);


int8_t hid_buttons[8];
int8_t hid_mouse_x;
int8_t hid_mouse_y;


#if HAL_USBH_USE_HID
#include "usbh/dev/hid.h"
#include "chprintf.h"

static THD_WORKING_AREA(waTestHID, 1024);

static void _hid_report_callback(USBHHIDDriver *hidp, uint16_t len) {
    uint8_t *report = (uint8_t *)hidp->config->report_buffer;

    if (hidp->type == USBHHID_DEVTYPE_BOOT_MOUSE) {

        hid_buttons[0] = report[0] & 1;
        hid_buttons[1] = report[0] & 2;
        hid_buttons[2] = report[0] & 4;
        hid_mouse_x += (int8_t)report[1];
        hid_mouse_y += (int8_t)report[2];

    	/*
        usbDbgPrintf("Mouse report: buttons=%02x, Dx=%d, Dy=%d",
                report[0],
                (int8_t)report[1],
                (int8_t)report[2]);
        */
    } else if (hidp->type == USBHHID_DEVTYPE_BOOT_KEYBOARD) {
    	/*
        usbDbgPrintf("Keyboard report: modifier=%02x, keys=%02x %02x %02x %02x %02x %02x",
                report[0],
                report[2],
                report[3],
                report[4],
                report[5],
                report[6],
                report[7]);
                */
    } else {
//        usbDbgPrintf("Generic report, %d bytes", len);
    }
}

static USBH_DEFINE_BUFFER(uint8_t report[HAL_USBHHID_MAX_INSTANCES][8]);
static USBHHIDConfig hidcfg[HAL_USBHHID_MAX_INSTANCES];

static void ThreadTestHID(void *p) {
    (void)p;
    uint8_t i;
    static uint8_t kbd_led_states[HAL_USBHHID_MAX_INSTANCES];

    for (i = 0; i < HAL_USBHHID_MAX_INSTANCES; i++) {
        hidcfg[i].cb_report = _hid_report_callback;
        hidcfg[i].protocol = USBHHID_PROTOCOL_BOOT;
        hidcfg[i].report_buffer = report[i];
        hidcfg[i].report_len = 8;
    }

    for (;;) {
        for (i = 0; i < HAL_USBHHID_MAX_INSTANCES; i++) {
            if (usbhhidGetState(&USBHHIDD[i]) == USBHHID_STATE_ACTIVE) {
                usbDbgPrintf("HID: Connected, HID%d", i);
                usbhhidStart(&USBHHIDD[i], &hidcfg[i]);
                if (usbhhidGetType(&USBHHIDD[i]) != USBHHID_DEVTYPE_GENERIC) {
                    usbhhidSetIdle(&USBHHIDD[i], 0, 0);
                }
                kbd_led_states[i] = 1;
            } else if (usbhhidGetState(&USBHHIDD[i]) == USBHHID_STATE_READY) {
                if (usbhhidGetType(&USBHHIDD[i]) == USBHHID_DEVTYPE_BOOT_KEYBOARD) {
                    USBH_DEFINE_BUFFER(uint8_t val);
                    val = kbd_led_states[i] << 1;
                    if (val == 0x08) {
                        val = 1;
                    }
                    usbhhidSetReport(&USBHHIDD[i], 0, USBHHID_REPORTTYPE_OUTPUT, &val, 1);
                    kbd_led_states[i] = val;
                }
            }
        }
        chThdSleepMilliseconds(200);
    }

}
#endif




void MY_USBH_Init(void) {

//	usbhmidiObjectInit(&USBHMIDID[0]);
	chThdCreateStatic(waTestMIDI, sizeof(waTestMIDI), NORMALPRIO,
			ThreadTestMIDI, 0);


#if HAL_USBH_USE_HID
    chThdCreateStatic(waTestHID, sizeof(waTestHID), NORMALPRIO, ThreadTestHID, 0);
#endif


	usbhStart(&USBHD2);

	// enable power...
	palSetPadMode(GPIOD, 7, PAL_MODE_OUTPUT_PUSHPULL);
	palClearPad(GPIOD, 7);
}



