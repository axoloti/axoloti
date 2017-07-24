#include "../ui.h"
#include "ui_nodes_common.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evtIsUp(evt))
		return list_nav_up(node);
	int n = node->objList.nobjs;
	if (evtIsDown(evt))
		return list_nav_down(node, n);
	if (node->objList.objs[menu_stack[menu_stack_position].currentpos].nparams
			> 0) {
		Parameter_t *p =
				node->objList.objs[menu_stack[menu_stack_position].currentpos].params;
		if ((evt.fields.button == btn_encoder)
				&& (evt.fields.quadrant == quadrant_topright))
			ProcessEncoderParameter(p, evt.fields.value);
		ProcessStepButtonsParameter(p);
	}
	if (evtIsEnter(evt) && (n > 0)) {
		// todo: use a stack of ObjMenu's
		ObjMenu.obj.obj =
				&(node->objList.objs)[menu_stack[menu_stack_position].currentpos];
		// copy object name, need a null terminated string now
		int i;
		for (i = 0; i < MAX_PARAMETER_NAME_LENGTH; i++) {
			((char *) ObjMenu.name)[i] = ObjMenu.obj.obj->name[i];
		}
		((char *) ObjMenu.name)[i] = 0;
		return ui_enter_node(&ObjMenu);
	}
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	ui_object_t *ui_objects;
	ui_objects = node->objList.objs;
	int l = node->objList.nobjs;
	if (flags & lcd_dirty_flag_listnav) {
		update_list_nav(l);
		return;
	}
	const int current_menu_position = menu_stack[menu_stack_position].currentpos;
	int offset = 0;
	if (current_menu_position > 3)
		offset = current_menu_position - 3;
	if ((l - current_menu_position) < 3)
		offset = l - 6;
	if (l < STATUSROW)
		offset = 0;
	if (ui_objects[current_menu_position].nparams > 0)
		ShowParameterOnButtonArrayLEDs(LED_STEPS,
				&ui_objects[current_menu_position].params[0]);
	LCD_drawChar(LCD_COL_RIGHT, 3, '-');
	LCD_drawChar(LCD_COL_RIGHT, 5, '+');
	int line;
	for (line = 0; line < (STATUSROW - 1); line++) {
		if (offset + line < l) {
			ui_object_t *obj = &ui_objects[offset + line];
			if (offset + line == current_menu_position) {
				LCD_drawStringInvN(LCD_COL_INDENT, line + 1, obj->name,
				MAX_PARAMETER_NAME_LENGTH);
				LCD_drawStringInvN(
				LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH, line + 1, "",
						3);
				if (obj->nparams) {
					drawParamValueInv(line + 1, LCD_VALUE_POSITION,
							obj->params);
					ShowParameterOnEncoderLEDRing(LED_RING_TOPRIGHT,
							obj->params);
				} else if (obj->ndisplays) {
					drawDispValueInv(line + 1, LCD_VALUE_POSITION,
							obj->displays);
					LED_clear(LED_RING_TOPRIGHT);
				} else {
					LED_clear(LED_RING_TOPRIGHT);
					LCD_drawStringN(LCD_VALUE_POSITION, line + 1, "", 8);
				}
			} else {
				LCD_drawStringN(LCD_COL_INDENT, line + 1, obj->name,
				MAX_PARAMETER_NAME_LENGTH);
				LCD_drawStringN(
				LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH, line + 1, "",
						3);
				if (obj->nparams) {
					drawParamValue(line + 1, LCD_VALUE_POSITION, obj->params);
				} else if (obj->ndisplays) {
					drawDispValue(line + 1, LCD_VALUE_POSITION, obj->displays);
				} else {
					LCD_drawStringN(LCD_VALUE_POSITION, line + 1, "", 8);
				}
			}
		} else
			// blank
			LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
	}
	LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
}

static void fpaint_line_initial(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawStringN(LCD_COL_EQ, y, "     0", LCD_COL_EQ_LENGTH);
}

static void fpaint_line_initial_inv(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawStringInvN(LCD_COL_EQ, y, "     0", LCD_COL_EQ_LENGTH);
}

const nodeFunctionTable nodeFunctionTable_object_list = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_initial,
		fpaint_line_initial_inv
};
