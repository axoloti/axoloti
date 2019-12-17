#include "ch.h"
#include "../ui.h"
#include "../axoloti_control.h"
#include "glcdfont.h"

static int offset = 0;

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (!evt.fields.value)
		return 0;
	if (evt.fields.button == btn_up)
		offset = offset > 0 ? offset - 1 : 0;
	else if (evt.fields.button == btn_down)
		offset++;
	return 0;
}

// optional extension: show thread states when
// the shift button is down or something.
// static const char *states[] = {CH_STATE_NAMES};

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	if (flags == lcd_dirty_flag_initial) {
		LCD_drawStringInvN(2, 1, "Name", 14);
		LCD_drawStringInvN(46, 1, "Free ", 5);
		LCD_drawChar(0, 7, CHAR_ARROW_UP);
		LCD_drawChar(10, 7, CHAR_ARROW_DOWN);
		return;
	}
	int j;

	thread_t *thd1 = chRegFirstThread();
	thread_t *thd = thd1;
	for (j = 0; j < offset; j++) {
		thd = chRegNextThread(thd);
		if (!thd) {
			LCD_drawStringN(2, 2, "<END>", 20);
			return;
		}
	}

	for (j = 2; j < 7; j++) {
		const char *name = "????";
		if (chRegGetThreadNameX(thd)) {
			name = chRegGetThreadNameX(thd);
		}
		int nfree = 0;
#if CH_DBG_FILL_THREADS
		int32_t *stk = (int32_t *)chThdGetWorkingAreaX(thd);
		while(*stk == 0x55555555) {
			nfree+=4;
			stk++;
		}
#endif
		LCD_drawStringN(2, j, name, 14);
		LCD_drawNumber5D(44, j, nfree);
		thd = chRegNextThread(thd);
		if (thd == thd1)
			break;
		if (!thd)
			break;
	}
	while (j < 7) {
		LCD_drawStringN(2, j, "", 20);
		j++;
	}
}

const nodeFunctionTable nodeFunctionTable_processes = {
		fhandle_evt,
		fpaint_screen_update,
		0, 0,
};
