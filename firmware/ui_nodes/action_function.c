#include "../ui.h"

static void fhandle_evt(const struct ui_node * node, ui_event evt) {
	if (evtIsEnter(evt)) {
		if (node->fnctn.fnctn != 0)
			(node->fnctn.fnctn)();
	}
}

static void fpaint_screen_initial(const struct ui_node * node) {
}

static void fpaint_screen_update(const struct ui_node * node) {
}

static void fpaint_line_initial(const struct ui_node * node, int y) {
}

static void fpaint_line_initial_inv(const struct ui_node * node, int y) {
}

static void fpaint_line_update(const struct ui_node * node, int y) {
}

static void fpaint_line_update_inv(const struct ui_node * node, int y) {
}

const nodeFunctionTable nodeFunctionTable_action_function = {
		fhandle_evt,
		fpaint_screen_initial,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv,
		fpaint_line_initial,
		fpaint_line_initial_inv
};
