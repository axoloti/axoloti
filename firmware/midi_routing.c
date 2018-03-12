#include "midi_routing.h"
#include "ch.h"
#include "ff.h"
#include "sdcard.h"
#include "exceptions.h"
#include <string.h>
#include "hal.h"
#include "chprintf.h"

FIL fil1;
char fn[64];

static void get_midi_routing_filename(char *dest, midi_routing_t *routing, enum direction dir) {
	if (dir == out) {
		chsnprintf(fn,64,"/settings/midi-out/%s.axr", routing->name);
	} else {
		chsnprintf(fn,64,"/settings/midi-in/%s.axr", routing->name);
	}
}

void load_midi_routing_default(midi_routing_t *routing, enum direction dir) {
	// fallback when no sdcard is present, or no map file found for device.
	// map first port to virtual port 1, unmap other ports.
	int i;
	if (!routing->nports) return;
	routing->bmvports[0] = 1;
	for(i=1;i<routing->nports;i++) {
		routing->bmvports[i] = 0;
	}
}

void load_midi_routing(midi_routing_t *routing, enum direction dir) {
	if (!fs_ready) {
		load_midi_routing_default(routing, dir);
	    return;
	}
    FRESULT err;
    get_midi_routing_filename(fn, routing, dir);
    err = f_open(&fil1, fn, FA_READ);
    if (err == FR_OK) {
		UINT bytes_read;
		f_read(&fil1, (uint8_t *)&routing->bmvports, routing->nports*4, &bytes_read);
		f_close(&fil1);
    } else {
		load_midi_routing_default(routing, dir);
    }
}

void store_midi_routing(midi_routing_t *routing, enum direction dir) {
	if (!fs_ready) {
	    return;
	}
	get_midi_routing_filename(fn, routing, dir);
	FRESULT err;
	err = f_open(&fil1, fn, FA_WRITE | FA_CREATE_ALWAYS);
	if (err != FR_OK) {
		report_fatfs_error(err, fn);
	} else {
		UINT bytes_written;
		f_write(&fil1, (uint8_t *)&routing->bmvports, routing->nports*4, &bytes_written);
		f_close(&fil1);
	}
}
