#include "../ui.h"
#include "ui_nodes_common.h"
#include "patch.h"
#include "qgfx.h"
#include "chprintf.h"
#include "glcdfont.h"

static bool canNavigateUp(void) {
	return (menu_stack[menu_stack_position].currentpos >= 4);
}

static bool canNavigateDown(void) {
	return ((menu_stack[menu_stack_position].currentpos + 4) < patchMeta.nparams);
}

int value_from_input_event(input_event evt) {
	switch (evt.fields.button) {
	case btn_encoder:
		return evt.fields.value;
	case btn_up:
		return 1;
	case btn_down:
		return -1;
	default:
		return 0;
	}
}

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (!evt.fields.value)
		return 0;
	if (evt.fields.quadrant == quadrant_main) {
		if (evt.fields.button == btn_up) {
			if (canNavigateUp()) {
				menu_stack[menu_stack_position].currentpos -= 4;
				return -1;
			}
		} else if (evt.fields.button == btn_down) {
			if (canNavigateDown()) {
				menu_stack[menu_stack_position].currentpos += 4;
				return -1;
			}
		}
	} else {
		int q = evt.fields.quadrant - quadrant_topleft;
		int i = q + menu_stack[menu_stack_position].currentpos;
		if (i < patchMeta.nparams) {
			int v = value_from_input_event(evt);
			if (v) {
				Parameter_t *p = &patchMeta.params[i];
				ProcessEncoderParameter(p, v);
				return lcd_dirty_flag_usr1 << q;
			}
		}
	}
	return 0;
}

void drawParamValue1(const gfxq *gfx, Parameter_t *param) {
	// TODO: other parameter types
	switch (param->type) {
	case param_type_bin_1bit_momentary:
	case param_type_bin_1bit_toggle:
		if (param->d.intt.value) {
			gfx->drawStringN(3, 1, "on", 8);
		} else {
			gfx->drawStringN(3, 1, "off", 8);
		}
		break;
	case param_type_bin_16bits:
//	   LCD_drawBitField2(x, line, param->d.intt.value, 16);
		break;
	case param_type_bin_32bits:
//	   LCD_drawBitField(x, line, param->d.intt.value, 32);
		break;
	case param_type_int: {
		char s[9];
		chsnprintf(&s[0], 8, "%7d", param->d.intt.value);
		gfx->drawStringN(3, 1, s, 8);
	}
		break;
	case param_type_frac_sq27:
	case param_type_frac_uq27: {
		char s[9];
		chsnprintf(&s[0], 8, "%2.2f", (1.0f / (1 << 21)) * param->d.frac.value);
		gfx->drawStringN(3, 1, s, 8);
		chsnprintf(&s[0], 8, "%08X", param->d.frac.value);
		gfx->drawStringN(3, 2, s, 8);
	}
		break;
	default: {
		char s[8];
		chsnprintf(&s[0], 8, "%08X", param->d.frac.value);
		gfx->drawStringN(3, 1, s, 8);
	}
		break;
	}
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flag) {
	unsigned int i = menu_stack[menu_stack_position].currentpos;
	switch (flag) {
	case 0: {
		int q = 0;
		while ((i < patchMeta.nparams) && (q < 4)) {
			Parameter_t *p = &patchMeta.params[i];
			if (p->signals & 0x00000004) {
				p->signals &= ~0x00000004;
				chEvtAddEvents(lcd_dirty_flag_usr1 << q);
			}
			i++;
			q++;
		}
	}
		break;
	case lcd_dirty_flag_initial: {
		int q = 0;
		while ((i < patchMeta.nparams) && (q < 4)) {
			gfx_Q[q++].drawStringInvN(3, 0,
					(const char *) &patchMeta.param_names[i++], 8);
		}
		if (canNavigateUp()) {
			LCD_drawChar(0, 7, CHAR_ARROW_UP);
		}
		if (canNavigateDown()) {
			LCD_drawChar(10, 7, CHAR_ARROW_DOWN);
		}
	}
		break;
	case lcd_dirty_flag_usr0: {
	}
		break;
	case lcd_dirty_flag_usr1: {
		if (i >= patchMeta.nparams)
			break;
		Parameter_t *p = &patchMeta.params[i];
		drawParamValue1(&gfx_Q[0], p);
		ShowParameterOnEncoderLEDRing(LED_RING_TOPLEFT, p);
	}
		break;
	case lcd_dirty_flag_usr2: {
		i += 1;
		if (i >= patchMeta.nparams)
			break;
		Parameter_t *p = &patchMeta.params[i];
		drawParamValue1(&gfx_Q[1], p);
		ShowParameterOnEncoderLEDRing(LED_RING_TOPRIGHT, p);
	}
		break;
	case lcd_dirty_flag_usr3: {
		i += 2;
		if (i >= patchMeta.nparams)
			break;
		Parameter_t *p = &patchMeta.params[i];
		drawParamValue1(&gfx_Q[2], p);
		ShowParameterOnEncoderLEDRing(LED_RING_BOTTOMLEFT, p);
	}
		break;
	case lcd_dirty_flag_usr4: {
		i += 3;
		if (i >= patchMeta.nparams)
			break;
		Parameter_t *p = &patchMeta.params[i];
		drawParamValue1(&gfx_Q[3], p);
		ShowParameterOnEncoderLEDRing(LED_RING_BOTTOMRIGHT, p);
	}
		break;
	case lcd_dirty_flag_usr5: {
	}
		break;
	default:
		break;
	}
}

const nodeFunctionTable nodeFunctionTable_param_list = {
		fhandle_evt,
		fpaint_screen_update,
		0,
		0
};
