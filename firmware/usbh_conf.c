#include "ch.h"
#include "hal.h"
#include "usbh_midi_core_lld.h"
#include "usbh_midi_core.h"


/* notes:
 * * debugging is active on SD2
 *   (ENABLE_SERIAL_DEBUG)
 * * midi output is not implemented yet
 * * midi in callback is working
 * * hal_usbh.c (from chibios) is modified:

extern const const usbh_classdriverinfo_t usbhmidiClassDriverInfo;

static const usbh_classdriverinfo_t *usbh_classdrivers_lookup[] = {
	&usbhmidiClassDriverInfo,
#if HAL_USBH_USE_FTDI
	&usbhftdiClassDriverInfo,
#endif
#if HAL_USBH_USE_IAD
	&usbhiadClassDriverInfo,
#endif
#if HAL_USBH_USE_UVC
	&usbhuvcClassDriverInfo,
#endif
#if HAL_USBH_USE_MSD
	&usbhmsdClassDriverInfo,
#endif
#if HAL_USBH_USE_HUB
	&usbhhubClassDriverInfo
#endif
};

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

void MY_USBH_Init(void) {
	// enable power...
	palSetPadMode(GPIOD, 7, PAL_MODE_OUTPUT_PUSHPULL);
	palClearPad(GPIOD, 7);

	usbhmidiObjectInit(&USBHMIDID[0]);
	chThdCreateStatic(waTestMIDI, sizeof(waTestMIDI), NORMALPRIO,
			ThreadTestMIDI, 0);
	usbhStart(&USBHD2);
}

int8_t hid_buttons[8];
int8_t hid_mouse_x;
int8_t hid_mouse_y;
