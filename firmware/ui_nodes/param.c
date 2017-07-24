#include "../ui.h"
#include "../axoloti_control.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	if ((evt.fields.button == btn_encoder)
			&& (evt.fields.quadrant == quadrant_topright))
		ProcessEncoderParameter(node->param.param, evt.fields.value);
	ProcessStepButtonsParameter(node->param.param);
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	LCD_clear();
	Parameter_t *p = node->param.param;
	Parameter_name_t *pn = node->param.param_name;
	int line = 1;
	LCD_drawStringN(LCD_COL_INDENT, line, pn->name,
	MAX_PARAMETER_NAME_LENGTH);
	line++;
	LCD_drawStringN(LCD_COL_INDENT, line, "type", 10);
	LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->type);
	line++;
	switch (p->type) {
	case param_type_frac_sq27:
	case param_type_frac_uq27:
		LCD_drawStringN(LCD_COL_INDENT, line, "value", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.value);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "modvalue", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.modvalue);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "finalvalue", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.finalvalue);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "offset", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.offset);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "multiplier", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.frac.multiplier);
		ShowParameterOnEncoderLEDRing(LED_RING_TOPRIGHT, p);
		break;
	case param_type_bin_1bit_momentary:
	case param_type_bin_1bit_toggle:
	case param_type_bin_16bits:
	case param_type_bin_32bits:
		ShowParameterOnButtonArrayLEDs(LED_STEPS, p);
		LCD_drawStringN(LCD_COL_INDENT, line, "value", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.value);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "modvalue", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.modvalue);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "finalvalue", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.finalvalue);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "nbits", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.bin.nbits);
		line++;
		break;
	case param_type_int:
		LCD_drawStringN(LCD_COL_INDENT, line, "value", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.value);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "modvalue", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.modvalue);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "finalvalue", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.finalvalue);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "minimum", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.minimum);
		line++;
		LCD_drawStringN(LCD_COL_INDENT, line, "maximum", 10);
		LCD_drawNumberHex32(LCD_VALUE_POSITION, line, p->d.intt.maximum);
		ShowParameterOnEncoderLEDRing(LED_RING_TOPRIGHT, p);
		break;
	default:
		LCD_drawStringN(LCD_COL_INDENT, line, "undefined", 10);
	}
}

static void fpaint_line_update(const struct ui_node * node, int y, uint32_t flags) {
}

static void fpaint_line_update_inv(const struct ui_node * node, int y, uint32_t flags) {
}

const nodeFunctionTable nodeFunctionTable_param = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv,
};
