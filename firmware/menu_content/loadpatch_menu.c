#include "loadpatch_menu.h"
#include "axoloti_control.h"
#include "ch.h"
#include "hal.h"
#include "ff.h"
#include "chprintf.h"
#include "sdcard.h"
#include "exceptions.h"
#include "pconnection.h"
#include "patch.h"

/*
 * todo: indicate patch load success/fail...
 * todo: nicer scrolling
 */

static int sel = 0;
static int nfiles = 0;

static void init(void) {
	sel = 0;
	nfiles = 0;
	sdcard_attemptMountIfUnmounted();
	if (!fs_ready) {
		LogTextMessage("sdcard not ready");
		return;
	}
	FRESULT res;
	DIR dir;
	FILINFO fno;
	res = f_opendir(&dir, "/");
	int i = 0;
	if (res != FR_OK) {
		report_fatfs_error(res,"");
		nfiles = 0;
		return;
	}
	for (;;) {
		res = f_readdir(&dir, &fno);
		if (res != FR_OK || fno.fname[0] == 0)
			break;
//		LogTextMessage("loadpatch_menu traversing %s", fno.fname);
		if (fno.fname[0] == '.')
			continue;
		char fn_patch[64];
		chsnprintf(fn_patch,64,"/%s/patch.bin",fno.fname);
		res = f_stat(fn_patch, (FILINFO*)0);
		if (res != FR_OK)
			continue;
		i++;
	}
	nfiles = i;
	f_closedir(&dir);
	// diagnostic log
//	f_getcwd(fno.fname, sizeof(fno.fname));
//	LogTextMessage("loadpatch_menu init %d cwd %s", nfiles, fno.fname);
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
	DIR dir;
	FILINFO fno;
	res = f_opendir(&dir, "/");
	if (res != FR_OK) {
		int i;
		for (i=0;i<7;i++) {
			LCD_drawStringN(0, 1 + i - sel, "////", 20);
		}
		return;
	}
	int i = 0;
	for (;;) {
		res = f_readdir(&dir, &fno);
		if (res != FR_OK || fno.fname[0] == 0)
			break;
		if (fno.fname[0] == '.')
			continue;
		// check if this directory contains a patch.bin file
		char fn_patch[64];
		chsnprintf(fn_patch,64,"/%s/patch.bin",fno.fname);
		res = f_stat(fn_patch, (FILINFO*)0);
		if (res != FR_OK)
			continue;
		if (i-sel == 0) {
			LCD_drawStringInvN(0, 1 + i - sel, fno.fname, 20);
			if (launch) {
				f_closedir(&dir);
				LoadPatch(fn_patch);
				return;
			}
		} else if (i - sel > 0)
			LCD_drawStringN(0, 1 + i - sel, fno.fname, 20);
		if (i - sel == 6)
			break;
		i++;
	}
	for (; i - sel < 7; i++) {
		LCD_drawStringN(0, 1 + i - sel, "////", 20);
	}
	f_closedir(&dir);
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
