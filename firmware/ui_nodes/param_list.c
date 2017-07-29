#include "../ui.h"
#include "ui_nodes_common.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evtIsUp(evt))
		return list_nav_up(node);
	int n = node->paramList.nparams;
	if (evtIsDown(evt))
		return list_nav_down(node, n);
	if (n > 0) {
		Parameter_t *p =
				&node->paramList.params[menu_stack[menu_stack_position].currentpos];
		if (evtIsEnter(evt)) {
			ParamMenu.param.param = p;
			ParamMenu.param.param_name =
					&node->paramList.param_names[menu_stack[menu_stack_position].currentpos];
			return ui_enter_node(&ParamMenu);
		}
		if ((evt.fields.button == btn_encoder)
				&& (evt.fields.quadrant == quadrant_topright))
			ProcessEncoderParameter(p, evt.fields.value);
		ProcessStepButtonsParameter(p);
	}
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	Parameter_t *params = node->paramList.params;
	Parameter_name_t *param_names = node->paramList.param_names;
	const int current_menu_position = menu_stack[menu_stack_position].currentpos;
	int l = node->paramList.nparams;
	if (flags & lcd_dirty_flag_listnav) {
		update_list_nav(l);
		return;
	}
	int offset = 0;
	if (current_menu_position > 3)
		offset = current_menu_position - 3;
	if ((l - current_menu_position) < 3)
		offset = l - 6;
	if (l < STATUSROW)
		offset = 0;
	if (l > 0)
		ShowParameterOnButtonArrayLEDs(LED_STEPS,
				&params[current_menu_position]);
	LCD_drawChar(LCD_COL_RIGHT, 3, '-');
	LCD_drawChar(LCD_COL_RIGHT, 5, '+');
	int line;
	for (line = 0; line < (STATUSROW - 1); line++) {
		if (offset + line < l) {
			if (offset + line == current_menu_position) {
				LCD_drawStringInvN(LCD_COL_INDENT, line + 1,
						param_names[offset + line].name,
						MAX_PARAMETER_NAME_LENGTH);
				LCD_drawStringInvN(
				LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH, line + 1, "",
						3);
				drawParamValueInv(line + 1, LCD_VALUE_POSITION,
						&params[offset + line]);
			} else {
				LCD_drawStringN(LCD_COL_INDENT, line + 1,
						param_names[offset + line].name,
						MAX_PARAMETER_NAME_LENGTH);
				LCD_drawStringN(
				LCD_COL_INDENT + 3 * MAX_PARAMETER_NAME_LENGTH, line + 1, "",
						3);
				drawParamValue(line + 1, LCD_VALUE_POSITION,
						&params[offset + line]);
			}
		} else
			// blank
			LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
	}
	LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
}

static void fpaint_line_initial(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawStringN(LCD_COL_EQ, y, "     $", LCD_COL_EQ_LENGTH);
}

static void fpaint_line_initial_inv(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawStringInvN(LCD_COL_EQ, y, "     $", LCD_COL_EQ_LENGTH);
}

const nodeFunctionTable nodeFunctionTable_param_list = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_initial,
		fpaint_line_initial_inv
};
