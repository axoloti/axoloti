#include "../ui.h"
#include "../axoloti_control.h"

static void fhandle_evt(const struct ui_node * node, ui_event evt) {
}

static void fpaint_screen_initial(const struct ui_node * node) {
}

static void fpaint_screen_update(const struct ui_node * node) {
}

static void fpaint_line_update(const struct ui_node * node, int y) {
	LCD_drawNumber5D(LCD_COL_VAL, y, *node->intValue.pvalue);
}

static void fpaint_line_update_inv(const struct ui_node * node, int y) {
	LCD_drawNumber5DInv(LCD_COL_VAL, y, *node->intValue.pvalue);
}

const nodeFunctionTable nodeFunctionTable_integer_value = {
		fhandle_evt,
		fpaint_screen_initial,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv,
		0,
		0
};
