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
	return ((menu_stack[menu_stack_position].currentpos + 4)
			< patchMeta.nobjects);
}

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evt.fields.quadrant == quadrant_main) {
		if (!evt.fields.value)
			return 0;
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
		if (!evt.fields.value)
			return 0;
		int q = evt.fields.quadrant - quadrant_topleft;
		int i = q + menu_stack[menu_stack_position].currentpos;
		if (i < patchMeta.nobjects) {
			if (evt.fields.button == btn_up) {
				ObjMenu.obj.obj = &patchMeta.objects[i];
				// copy object name, need a null terminated string now
				int i;
				for (i = 0; i < MAX_PARAMETER_NAME_LENGTH; i++) {
					((char *) ObjMenu.name)[i] = ObjMenu.obj.obj->name[i];
				}
				((char *) ObjMenu.name)[i] = 0;
				return ui_enter_node(&ObjMenu);
			} else if (patchMeta.objects[i].nparams) {
				int v = getValuFromInputEvent(evt);
				if (v) {
					Parameter_t *p = &patchMeta.objects[i].params[0];
					ProcessEncoderParameter(p, v);
				}
			}
		}
	}
	return 0;
}

void drawDispValue1(const gfxq *gfx, Display_meta_t *disp) {
	int x, line;
	if (gfx == &gfx_Q[0]) {
		x = 0;
		line = 2;
	} else if (gfx == &gfx_Q[1]) {
		x = 32;
		line = 2;
	} else if (gfx == &gfx_Q[2]) {
		x = 0;
		line = 5;
	} else if (gfx == &gfx_Q[3]) {
		x = 32;
		line = 5;
	} else
		return;

	switch (disp->display_type) {
	case display_meta_type_int32:
		LCD_drawNumber7D(x, line, *disp->displaydata);
		break;
	case display_meta_type_ibar16:
		LCD_drawHBar(x, line, *disp->displaydata, 16);
		break;
	case display_meta_type_ibar32:
		LCD_drawHBar(x, line, *disp->displaydata, 32);
		break;
	case display_meta_type_chart_sq27:
	case display_meta_type_dial_sq27:
		// TODO: create signed bar
		LCD_drawHBar(x, line, __SSAT(*disp->displaydata >> 21, 5) + 16, 32);
		break;
	case display_meta_type_chart_uq27:
	case display_meta_type_dial_uq27:
		LCD_drawHBar(x, line, __USAT(*disp->displaydata >> 21, 5), 32);
		break;
	default:
		LCD_drawNumberHex32(x, line, *disp->displaydata);
	}
}

static void paint_update_quadrant(int quadrant_index, int obj_index) {
	if (obj_index >= patchMeta.nobjects)
		return;
	ui_object_t *obj = &patchMeta.objects[obj_index];
	if (obj->nparams) {
		Parameter_t *p = &obj->params[0];
		drawParamValue1(&gfx_Q[quadrant_index], p);
		switch (quadrant_index) {
		case 0:
			ShowParameterOnEncoderLEDRing(LED_RING_TOPLEFT, p);
			break;
		case 1:
			ShowParameterOnEncoderLEDRing(LED_RING_TOPRIGHT, p);
			break;
		case 2:
			ShowParameterOnEncoderLEDRing(LED_RING_BOTTOMLEFT, p);
			break;
		case 3:
			ShowParameterOnEncoderLEDRing(LED_RING_BOTTOMRIGHT, p);
			break;
		}
	} else if (obj->ndisplays) {
		drawDispValue1(&gfx_Q[quadrant_index], &obj->displays[0]);
	}
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flag) {
	int i = menu_stack[menu_stack_position].currentpos;
	switch (flag) {
	case 0: { // idle
		static int q1 = 0;
		q1 = (q1 + 1) & 0x3; // 4 quadrants
		i += q1;
		if (i < patchMeta.nobjects) {
			if (patchMeta.objects[i].nparams) {
				Parameter_t *p = &patchMeta.objects[i].params[0];
				if (p->signals & 0x00000004) {
					p->signals &= ~0x00000004;
					chEvtAddEvents(lcd_dirty_flag_usr1 << q1);
				}
			} else if (patchMeta.objects[i].ndisplays) {
				chEvtAddEvents(lcd_dirty_flag_usr1 << q1);
			}
		}
	}
		break;
	case lcd_dirty_flag_initial: {
		int q = 0;
		while ((i < patchMeta.nobjects) && (q < 4)) {
			ui_object_t *o = &patchMeta.objects[i++];
			gfx_Q[q].drawStringInvN(3, 0, (const char *) &o->name, 8);
			if (o->nparams) {
				gfx_Q[q].drawChar(0, 0, CHAR_ARROW_UP);
				gfx_Q[q].drawChar(0, 2, CHAR_ARROW_DOWN);
				// perhaps we could also show parameter/display name in addition to the object name?
				// gfx_Q[q].drawStringN(3, 1, (const char *)&o->param_names[0], 8);
			}
			q++;
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
		paint_update_quadrant(0, i);
	}
		break;
	case lcd_dirty_flag_usr2: {
		paint_update_quadrant(1, i + 1);
	}
		break;
	case lcd_dirty_flag_usr3: {
		paint_update_quadrant(2, i + 2);
	}
		break;
	case lcd_dirty_flag_usr4: {
		paint_update_quadrant(3, i + 3);
	}
		break;
	case lcd_dirty_flag_usr5: {
	}
		break;
	default:
		break;
	}
}

const nodeFunctionTable nodeFunctionTable_object_list = {
		fhandle_evt,
		fpaint_screen_update,
		0,
		0
};
