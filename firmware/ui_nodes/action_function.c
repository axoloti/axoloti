#include "../ui.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if (evtIsEnter(evt)) {
		if (node->fnctn.fnctn != 0)
			(node->fnctn.fnctn)();
	}
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
}

static void fpaint_line_update(const struct ui_node * node, int y, uint32_t flags) {
}

static void fpaint_line_update_inv(const struct ui_node * node, int y, uint32_t flags) {
}

const nodeFunctionTable nodeFunctionTable_action_function = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv,
};
