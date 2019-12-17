#include "../ui.h"
#include "../axoloti_control.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
}

static void fpaint_line_update(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawNumber5D(LCD_COL_VAL, y, *node->intValue.pvalue);
}

static void fpaint_line_update_inv(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawNumber5DInv(LCD_COL_VAL, y, *node->intValue.pvalue);
}

const nodeFunctionTable nodeFunctionTable_integer_value = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv
};
