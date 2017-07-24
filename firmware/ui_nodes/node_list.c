#include "../ui.h"
#include "../axoloti_control.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evtIsUp(evt)) return list_nav_up(node);
	if (evtIsDown(evt)) return list_nav_down(node, node->nodeList.length);
	if (evtIsEnter(evt) && node->nodeList.length) {
		ui_node_t *cur =
				&((ui_node_t *) (node->nodeList.array))[menu_stack[menu_stack_position].currentpos];
		return ui_enter_node(cur);
	}
	return 0;
	/*
	if ((cur->node_type == node_type_integer_value)) {
		if (EncBuffer[1] > 0) {
			UINode_Increment(cur);
			EncBuffer[1]--;
		}
		if (EncBuffer[1] < 0) {
			UINode_Decrement(cur);
			EncBuffer[1]++;
		}
	}
	*/
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	const int current_menu_position = menu_stack[menu_stack_position].currentpos;
	int l = node->nodeList.length;

	ui_node_t *k = (ui_node_t *) node->nodeList.array;
	int offset = 0;
	if (current_menu_position > 3)
		offset = current_menu_position - 3;
	if ((l - current_menu_position) < 3)
		offset = l - 6;
	if (l < STATUSROW)
		offset = 0;

	if (flags & lcd_dirty_flag_listnav) {
		update_list_nav(l);
		int line;
		for (line = 0; line < (STATUSROW - 1); line++) {
			if (offset + line < l) {
				ui_node_t *lnode = &k[offset + line];
				if (offset + line == current_menu_position) {
					LCD_drawStringInvN(LCD_COL_INDENT, line+1, lnode->name, 14); // todo: fix 14
					if (lnode->functions->paint_line_update_inv)
						lnode->functions->paint_line_update_inv(lnode, line+1, 1);
					else
						LCD_drawStringInvN(LCD_COL_EQ, line+1, "", LCD_COL_EQ_LENGTH);
				}
				else {
					LCD_drawStringN(LCD_COL_INDENT, line+1, lnode->name, 14); // todo: fix 14
					if (lnode->functions->paint_line_update)
						lnode->functions->paint_line_update(lnode, line+1, 1);
					else
						LCD_drawStringN(LCD_COL_EQ, line+1, "", LCD_COL_EQ_LENGTH);
				}
			} else
				// blank
				LCD_drawStringN(LCD_COL_INDENT, line + 1, "", 19);
		}
		return;
	} else {
		int line;
		for (line = 0; line < (STATUSROW - 1); line++) {
			if (offset + line < l) {
				ui_node_t *lnode = &k[offset + line];
				if (offset + line == current_menu_position) {
					if (lnode->functions->paint_line_update_inv)
						lnode->functions->paint_line_update_inv(lnode, line+1, 1);
				}
				else {
					if (lnode->functions->paint_line_update)
						lnode->functions->paint_line_update(lnode, line+1, 1);
				}
			}
		}
	}
//	ui_node_t * sel = &k[menu_stack[menu_stack_position].currentpos];
/*
	if ((sel->node_type == node_type_node_list)
			|| (sel->node_type == node_type_custom)
			|| (sel->node_type == node_type_param_list)
			|| (sel->node_type == node_type_action_function
					&& sel->fnctn.fnctn != 0))
		LCD_drawStringInv(LCD_COL_ENTER, STATUSROW, "ENTER");
	else
		LCD_drawString(LCD_COL_ENTER, STATUSROW, "     ");
*/
}

static void fpaint_line_initial(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawStringN(LCD_COL_EQ, y, "     *", LCD_COL_EQ_LENGTH);
}

static void fpaint_line_initial_inv(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawStringInvN(LCD_COL_EQ, y, "     *", LCD_COL_EQ_LENGTH);
}

const nodeFunctionTable nodeFunctionTable_node_list = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_initial,
		fpaint_line_initial_inv
};
