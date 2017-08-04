#include "loadpatch_menu.h"
#include "axoloti_control.h"
#include "ff.h"

/*
 * currently lists all directories
 * including those that do not contain a patch.bin
 * those will fail to load
 *
 * todo: indicate load success/fail...
 * todo: nicer scrolling
 */

typedef struct {
  DIR dir;
  FILINFO fno;
   char FileName[64];
} data;

static data d;

static int sel = 0;
static int nfiles = 0;


static void init(void) {
	sel = 0;
	nfiles = 0;
	FRESULT res;
	d.fno.lfname = &d.FileName[0];
	d.fno.lfsize = sizeof(d.FileName);
	res = f_opendir(&d.dir, "/");
	int i = 0;
	if (res == FR_OK) {
		for (;;) {
			res = f_readdir(&d.dir, &d.fno);
			if (res != FR_OK || d.fno.fname[0] == 0)
				break;
			if (d.fno.fname[0] == '.')
				continue;
			char *fn;
			fn = *d.fno.lfname ? d.fno.lfname : d.fno.fname;
			if (fn[0] == '.')
				continue;
			i++;
		}
	}
	nfiles = i;
}

static void refresh(int launch) {
	if (nfiles == 0) {
		int i;
		LCD_drawStringN(0, 1, "No patches...", 20);
		for(i=2;i<8;i++)
			LCD_drawStringN(0, i, "", 20);
		return;
	}
	FRESULT res;
	d.fno.lfname = &d.FileName[0];
	d.fno.lfsize = sizeof(d.FileName);
	res = f_opendir(&d.dir, "/");
	int i = 0;
	if (res == FR_OK) {
		for (;;) {
			res = f_readdir(&d.dir, &d.fno);
			if (res != FR_OK || d.fno.fname[0] == 0)
				break;
			if (d.fno.fname[0] == '.')
				continue;
			char *fn;
			fn = *d.fno.lfname ? d.fno.lfname : d.fno.fname;
			if (fn[0] == '.')
				continue;
			if (i-sel == 0) {
				LCD_drawStringInvN(0, 1 + i - sel, fn, 20);
				if (launch) {
					char *f0 = fn;
					for(i=0;i<sizeof(d.FileName);i++) {
						if (!*f0) break;
						f0++;
					}
					if ((!*f0)&&(i<sizeof(d.FileName)-11)) {
						*f0++ = '/';
						*f0++ = 'p';
						*f0++ = 'a';
						*f0++ = 't';
						*f0++ = 'c';
						*f0++ = 'h';
						*f0++ = '.';
						*f0++ = 'b';
						*f0++ = 'i';
						*f0++ = 'n';
						*f0++ = 0;
						LoadPatch(fn);
					} else {
						LCD_drawStringInvN(0, 1 + i - sel, "filename too long!", 20);
					}
				}
			} else if (i - sel > 0)
				LCD_drawStringN(0, 1 + i - sel, fn, 20);
			if (i - sel == 6)
				break;
			i++;
		}
	}
	for (; i - sel < 7; i++) {
		LCD_drawStringN(0, 1 + i - sel, "////", 20);
	}
}

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (!evt.fields.value)
		return 0;

	if ((evt.fields.button == btn_encoder)
			&& (evt.fields.quadrant == quadrant_topleft)) {
		sel = sel + evt.fields.value;
		if (sel >= nfiles) sel = nfiles-1;
		if (sel < 0) sel = 0;
		return lcd_dirty_flag_usr1;
	}
	if (evt.fields.button == btn_down) {
		sel = sel + 1;
		if (sel == nfiles) {
			sel = nfiles - 1;
		}
		return lcd_dirty_flag_usr1;
	}
	if (evt.fields.button == btn_up) {
		sel = (sel > 1) ? sel - 1 : 0;
		return lcd_dirty_flag_usr1;
	}
	if (evt.fields.button == btn_E) {
		refresh(TRUE);
	}
	return 0;
}


static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	if (!flags) {
		return;
	} else if (flags == lcd_dirty_flag_usr1) {
		refresh(0);
	} else if (flags == lcd_dirty_flag_initial) {
		init();
	}
}

nodeFunctionTable nodeFunctionTable_loadpatch = {
		fhandle_evt,
		fpaint_screen_update,
		0,
		0,
};
