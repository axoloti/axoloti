#include "ch.h"
#include "hal.h"
#include "chprintf.h"
#include "string.h"
#include "../ui.h"
#include "../patch.h"
#include "../axoloti_control.h"
#include "../glcdfont.h"
#include "../qgfx.h"

typedef void (*adjustFunction)(input_event evt);
typedef int (*printFunction)(char *c, int n);

typedef struct {
	char *name;
	adjustFunction adjust;
	printFunction printVal;
	printFunction printVal2;
} qx;

static void adjustBPM(input_event evt) {
	midi_clock.period -= evt.fields.value;
	if (midi_clock.period<80) midi_clock.period=80;
	if (midi_clock.period>(255+80)) midi_clock.period = 255+80;
}

static int printBPM(char *c, int n) {
	float t = midi_clock.period/6000.0;
	float t24 = t*24; // time for 1 quarter note
	float f = 60.0/t24; // quarter notes in 60s
	chsnprintf(c,n,"%3.2f/m",f);
	return 255-(midi_clock.period-80);
}

static int printBPM2(char *c, int n) {
	chsnprintf(c,n,"%3d",midi_clock.period);
	return 0;
}

static int printDo(char *c, int n) {
	if (midi_clock.active)
		strncpy(c,"Running",n);
	else
		strncpy(c,"Halted",n);
	return -1;
}

static void doStartStop(input_event evt) {
	if (evt.fields.button == btn_up) {
		// start
		midi_clock.active = 1;
	} else if (evt.fields.button == btn_down) {
		// stop
		midi_clock.active = 0;
	}
}

static int printStop(char *c, int n) {
	strncpy(c,"Stop",n);
	return -1;
}

static int scroll = 0;

static qx q[4] = {
		{"BPM", adjustBPM, printBPM, printBPM2},
		{"Start", doStartStop, printDo, printStop},
		{"", 0, 0, 0},
		{"", 0, 0, 0}
};

static uint32_t fhandle_evt_midiclock(const struct ui_node * node, input_event evt) {
	if (!evt.fields.value) return 0;
	int k = evt.fields.quadrant - quadrant_topleft;
	if (k<0) return 0;
	if (!q[k].adjust) return 0;
	int r = k + scroll;
	if (evt.fields.button == btn_down) evt.fields.value = -1;
	q[r].adjust(evt);
	switch(k){
	case 0:
		return lcd_dirty_flag_usr0;
	case 1:
		return lcd_dirty_flag_usr1;
	case 2:
		return lcd_dirty_flag_usr2;
	case 3:
		return lcd_dirty_flag_usr3;
	}
	return 0;
}

static void update(int i) {
	char str[9];
	if (q[i].printVal) {
		int r = q[i+scroll].printVal(str,9);
		gfx_Q[i].drawStringN(4,1,str,9);
		if (r == -1) {
			gfx_Q[i].drawChar(0,0,'*');
			gfx_Q[i].drawChar(0,2,' ');
			gfx_Q[i].setEncoderOne(-1);
		} else {
			gfx_Q[i].drawChar(0,0,(r < 255)?CHAR_ARROW_UP:' ');
			gfx_Q[i].drawChar(0,2,(r > 0)?CHAR_ARROW_DOWN:' ');
			gfx_Q[i].setEncoderOne(r>>4);
		}
	}
	if (q[i].printVal2) {
		int r = q[i+scroll].printVal2(str,9);
		if (r == -1) {
			gfx_Q[i].drawChar(0,2,'*');
		}
		gfx_Q[i].drawStringN(4,2,str,9);
	}
}

static void fpaint_screen_update_midiclock(const struct ui_node * node,
		uint32_t flags) {
	if (flags == lcd_dirty_flag_initial) {
		int i;
		for(i=0;i<4;i++) {
			gfx_Q[i].drawStringN(4,0,q[i+scroll].name,9);
		}
	   return;
	} else if (flags == lcd_dirty_flag_usr0) {
		update(0);
	} else if (flags == lcd_dirty_flag_usr1) {
		update(1);
	} else if (flags == lcd_dirty_flag_usr2) {
		update(2);
	} else if (flags == lcd_dirty_flag_usr3) {
		update(3);
	}
}

const nodeFunctionTable nodeFunctionTable_midiclock = {
		fhandle_evt_midiclock,
		fpaint_screen_update_midiclock,
		0, 0,
};

