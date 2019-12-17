#include "../ui.h"
#include "../axoloti_control.h"

static uint32_t fhandle_evt(const struct ui_node * node, input_event evt) {
	return 0;
}

static void fpaint_screen_update(const struct ui_node * node, uint32_t flags) {
	// little oscilloscope!
	static int xpos = 0;
	if (flags & lcd_dirty_flag_initial) {
		xpos = 0;
	} else {
		uint8_t *p = &lcd_buffer[xpos + LCDWIDTH];
		int16_t *pvalue = node->shortValue.pvalue;
		const int ylim = 6 * 8 - 1;
		if (pvalue) {
			LCD_drawNumber5D(20, 7, *pvalue);
			int32_t y = ylim
					- (((*pvalue - node->shortValue.minvalue) * 3
							* node->shortValue.scale) >> 16);
			int v = LCDROWS - 2;
			if (xpos & 1) {
				uint32_t uy = y;
				while (v--) {
					if (uy < 8)
						*p = 1 << uy;
					else
						*p = 0;
					uy = uy - 8;
					p += LCDWIDTH;
				}
			} else {
				// with zero-line stipples, and clamped y
				if (y < 0)
					y = 0;
				if (y > ylim)
					y = ylim;
				uint32_t uy = y;
				uint32_t y0 = ylim
						- (((0 - node->shortValue.minvalue) * 3
								* node->shortValue.scale) >> 16);
				while (v--) {
					int v;
					if (uy < 8)
						v = 1 << uy;
					else
						v = 0;
					if (y0 < 8)
						v |= 1 << y0;
					*p = v;
					uy = uy - 8;
					y0 = y0 - 8;
					p += LCDWIDTH;
				}
			}
			xpos = (xpos + 1) & (LCDWIDTH - 1);
		}
	}
}

static void fpaint_line_update(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawNumber5D(LCD_COL_VAL, y, *node->shortValue.pvalue);
}

static void fpaint_line_update_inv(const struct ui_node * node, int y, uint32_t flags) {
	LCD_drawNumber5DInv(LCD_COL_VAL, y, *node->shortValue.pvalue);
}

const nodeFunctionTable nodeFunctionTable_short_value = {
		fhandle_evt,
		fpaint_screen_update,
		fpaint_line_update,
		fpaint_line_update_inv,
};
