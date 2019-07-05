#include "../ui.h"
#include "ui_nodes_common.h"
#include "patch.h"
#include "qgfx.h"
#include "chprintf.h"
#include "glcdfont.h"

void drawParamValue1(const gfxq *gfx, Parameter_t *param) {
	// TODO: other parameter types
	switch (param->type) {
	case param_type_bin_1bit_momentary:
	case param_type_bin_1bit_toggle:
		if (param->d.intt.value) {
			gfx->drawStringN(3, 1, "on", 8);
		} else {
			gfx->drawStringN(3, 1, "off", 8);
		}
		break;
	case param_type_bin_16bits:
//	   LCD_drawBitField2(x, line, param->d.intt.value, 16);
		break;
	case param_type_bin_32bits:
//	   LCD_drawBitField(x, line, param->d.intt.value, 32);
		break;
	case param_type_int: {
		char s[9];
		chsnprintf(&s[0], 8, "%7d", param->d.intt.value);
		gfx->drawStringN(3, 1, s, 8);
	}
		break;
	case param_type_frac_sq27:
	case param_type_frac_uq27: {
		char s[9];
		chsnprintf(&s[0], 8, "%2.2f", (1.0f / (1 << 21)) * param->d.frac.value);
		gfx->drawStringN(3, 1, s, 8);
		chsnprintf(&s[0], 8, "%08X", param->d.frac.value);
		gfx->drawStringN(3, 2, s, 8);
	}
		break;
	default: {
		char s[8];
		chsnprintf(&s[0], 8, "%08X", param->d.frac.value);
		gfx->drawStringN(3, 1, s, 8);
	}
		break;
	}
}
