#include "../ui.h"
#include "ui_nodes_common.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evtIsUp(evt))
		return list_nav_up(node);
	int n = node->obj.obj->nparams + node->obj.obj->ndisplays;
	if (evtIsDown(evt))
		return list_nav_down(node, n);
	int nparams = node->obj.obj->nparams;
	if (nparams > 0) {
		int pos = menu_stack[menu_stack_position].currentpos;
		if (pos < nparams) {
			Parameter_t *p = &(node->obj.obj->params)[pos];
			if (evtIsEnter(evt)) {
				Parameter_name_t *pn =
						&(node->obj.obj->param_names)[menu_stack[menu_stack_position].currentpos];
				ParamMenu.param.param = p;
				ParamMenu.param.param_name = pn;
				return ui_enter_node(&ParamMenu);
			}
			if ((evt.fields.button == btn_encoder)
					&& (evt.fields.quadrant == quadrant_topright))
				ProcessEncoderParameter(p, evt.fields.value);
			ProcessStepButtonsParameter(p);
		}
	}
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	ui_object_t *ui_object;
	ui_object = node->obj.obj;
	int l = ui_object->nparams + ui_object->ndisplays;
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
	LCD_drawChar(LCD_COL_RIGHT, 3, '-');
	LCD_drawChar(LCD_COL_RIGHT, 5, '+');
	int line;
	for (line = 0; line < (STATUSROW - 1); line++) {
		if (offset < l) {
			if (offset == current_menu_position)
				if (offset < ui_object->nparams) {
					LCD_drawStringInvN(LCD_COL_INDENT, line + 1,
							ui_object->param_names[offset].name,
							MAX_PARAMETER_NAME_LENGTH);
					LCD_drawStringInvN(
					LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH, line + 1,
							"", 3);
					drawParamValueInv(line + 1, LCD_VALUE_POSITION,
							&ui_object->params[offset]);
				} else {
					LCD_drawStringInvN(LCD_COL_INDENT, line + 1,
							ui_object->displays[offset - ui_object->nparams].name,
							MAX_PARAMETER_NAME_LENGTH);
					LCD_drawStringN(
					LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH, line + 1,
							"", 3);
					drawDispValueInv(line + 1, LCD_VALUE_POSITION,
							&ui_object->displays[offset - ui_object->nparams]);
				}
			else if (offset < ui_object->nparams) {
				LCD_drawStringN(LCD_COL_INDENT, line + 1,
						ui_object->param_names[offset].name,
						MAX_PARAMETER_NAME_LENGTH);
				drawParamValue(line + 1, LCD_VALUE_POSITION,
						&ui_object->params[offset]);
			} else {
				LCD_drawStringN(LCD_COL_INDENT, line + 1,
						ui_object->displays[offset - ui_object->nparams].name,
						MAX_PARAMETER_NAME_LENGTH);
				drawDispValue(line + 1, LCD_VALUE_POSITION,
						&ui_object->displays[offset - ui_object->nparams]);
			}
		} else
			// blank line
			LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
		offset++;
	}
	if (current_menu_position < ui_object->nparams) {
		Parameter_t *p = &ui_object->params[current_menu_position];
		ShowParameterOnEncoderLEDRing(LED_RING_TOPRIGHT, p);
		ShowParameterOnButtonArrayLEDs(LED_STEPS, p);
	}
	LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
}


const nodeFunctionTable nodeFunctionTable_object = {
		fhandle_evt,
		fpaint_screen_update,
		0,
		0
};
