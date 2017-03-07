/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
#include "ch.h"
#include "hal.h"
#include "axoloti_control.h"
#include "axoloti_board.h"
#include "ui.h"
#include <string.h>

uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS] SRAM2;
led_outputs_t leds[LEDSIZE] SRAM2;

void axoloti_control_init(void) {
	LCD_clearDisplay();
	LED_clear();
}

#define _BV(bit) (1 << (bit))

void LED_clear() {
	int c;
	for (c = 0; c < LEDSIZE; c++) {
		leds[c].led_32b = 0;
	}
}

void LED_setAll(unsigned c, int32_t v) {
	if (c < LEDSIZE) {
		leds[c].led_32b = v;
	}
}

void LED_setOne(unsigned c, unsigned b, unsigned v) {
	if (c < LEDSIZE && b < 16) {
		leds[c].led_32b &= ~(0x3 << (b * 2));
		leds[c].led_32b |= ((v & 0x3) << (b * 2));
	}
}

// the most basic function, set a single pixel
void LCD_drawPixel(int x, int y, uint16_t color) {
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return;

	// x is which column
	if (color)
		lcd_buffer[LCDHEADER + x + (y / 8) * LCDWIDTH] |= _BV(y % 8);
	else
		lcd_buffer[LCDHEADER + x + (y / 8) * LCDWIDTH] &= ~_BV(y % 8);
}

void LCD_setPixel(int x, int y) {
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return;
	lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)] |= _BV(y % 8);
}

void LCD_clearPixel(int x, int y) {
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return;
	lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)] &= ~_BV(y % 8);
}

uint8_t LCD_getPixel(int x, int y) {
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return 0;

	return (lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)]
			>> (y % 8)) & 0x1;
}

// clear everything
void LCD_clearDisplay(void) {
	int i;
	for (i = 0; i < LCDROWS; i++)
		memset(&lcd_buffer[LCDHEADER + i * LCDWIDTH], 0, LCDWIDTH);
}

extern const unsigned char font[];

void LCD_drawChar(int x, int line, unsigned char c) {
	if ((x < 0) || (x >= (LCDWIDTH - 5)) || (line < 0) || (line >= (LCDROWS)))
		return;
	int i = c * 5;
	int j = LCDHEADER + x + line * (LCDWIDTH + LCDHEADER);
	lcd_buffer[j++] = font[i++];
	lcd_buffer[j++] = font[i++];
	lcd_buffer[j++] = font[i++];
	lcd_buffer[j++] = font[i++];
	lcd_buffer[j++] = font[i++];
	lcd_buffer[j] = 0;
}

void LCD_drawCharInv(int x, int line, unsigned char c) {
	if ((x < 0) || (x >= (LCDWIDTH - 5)) || (line < 0)
			|| (line >= (LCDHEIGHT / 8)))
		return;
	int i = c * 5;
	int j = LCDHEADER + x + line * (LCDHEADER + LCDWIDTH);
	lcd_buffer[j++] = ~font[i++];
	lcd_buffer[j++] = ~font[i++];
	lcd_buffer[j++] = ~font[i++];
	lcd_buffer[j++] = ~font[i++];
	lcd_buffer[j++] = ~font[i++];
	lcd_buffer[j] = 0xFF;
}

void LCD_drawNumber3D(int x, int line, int i) {
	if (i < 0) {
		LCD_drawChar(x, line, '-');
		i = -i;
	} else
		LCD_drawChar(x, line, ' ');

	LCD_drawChar(x + 18, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawChar(x + 12, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawChar(x + 6, line, '0' + i);
}

void LCD_drawNumber3DInv(int x, int line, int i) {
	if (i < 0) {
		LCD_drawCharInv(x, line, '-');
		i = -i;
	} else
		LCD_drawCharInv(x, line, ' ');

	LCD_drawCharInv(x + 18, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawCharInv(x + 12, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawCharInv(x + 6, line, '0' + i);
}

void LCD_drawNumber5D(int x, int line, int i) {
	if (i < 0) {
		LCD_drawChar(x, line, '-');
		i = -i;
	} else
		LCD_drawChar(x, line, ' ');

	LCD_drawChar(x + 30, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawChar(x + 24, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawChar(x + 18, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawChar(x + 12, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawChar(x + 6, line, '0' + i);
}

void LCD_drawNumber5DInv(int x, int line, int i) {
	if (i < 0) {
		LCD_drawCharInv(x, line, '-');
		i = -i;
	} else
		LCD_drawCharInv(x, line, ' ');

	LCD_drawCharInv(x + 30, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawCharInv(x + 24, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawCharInv(x + 18, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawCharInv(x + 12, line, '0' + (i % 10));
	i = i / 10;
	LCD_drawCharInv(x + 6, line, '0' + i);
}

__STATIC_INLINE char hexchar_from_nibble(int x) {
	return (x < 10 ? '0' + x : 'A' - 10 + x);
}

void LCD_drawNumberHex32(int x, int line, uint32_t i) {
	if ((line < 0) || (line >= (LCDHEIGHT)) || (x < 0) || (x > (LCDWIDTH - 48)))
		return;
	char d;
	d = (i >> 28) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i >> 24) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i >> 20) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i >> 16) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i >> 12) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i >> 8) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i >> 4) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
	x += 6;
	d = (i) & 0xF;
	LCD_drawChar(x, line, hexchar_from_nibble(d));
}

void LCD_drawString(int x, int line, const char *str) {
	if ((line < 0) || (line >= (LCDROWS)) || (x < 0))
		return;
	unsigned char c;
	int j = LCDHEADER + x + line * (LCDWIDTH + LCDHEADER);
	lcd_buffer[j++] = 0x00;
	int x2 = x;
	while ((c = *str++)) {
		x2 += 6;
		if (x2 >= LCDWIDTH)
			return;
		int i = c * 5;
		lcd_buffer[j++] = font[i++];
		lcd_buffer[j++] = font[i++];
		lcd_buffer[j++] = font[i++];
		lcd_buffer[j++] = font[i++];
		lcd_buffer[j++] = font[i++];
		lcd_buffer[j++] = 0;
	}
}

void LCD_drawStringInv(int x, int line, const char *str) {
	if ((line < 0) || (line >= (LCDROWS)) || (x < 0))
		return;
	unsigned char c;
	int j = LCDHEADER + x + line * (LCDHEADER + LCDWIDTH);
	lcd_buffer[j++] = 0xFF;
	int x2 = x;
	while ((c = *str++)) {
		x2 += 6;
		if (x2 >= LCDWIDTH)
			return;
		int i = c * 5;
		lcd_buffer[j++] = ~font[i++];
		lcd_buffer[j++] = ~font[i++];
		lcd_buffer[j++] = ~font[i++];
		lcd_buffer[j++] = ~font[i++];
		lcd_buffer[j++] = ~font[i++];
		lcd_buffer[j++] = 0xFF;
	}
}

void LCD_drawStringN(int x, int line, const char *str, int xend) {
	if ((line < 0) || (line >= (LCDROWS)) || (x < 0))
		return;
	unsigned char c = *str++;
	uint8_t *p = &lcd_buffer[LCDHEADER + x + line * (LCDHEADER + LCDWIDTH)];
	*p++ = 0;
	x++;
	while (c) {
		if (x + 6 >= xend)
			break;
		x += 6;
		int i = c * 5;
		*p++ = font[i++];
		*p++ = font[i++];
		*p++ = font[i++];
		*p++ = font[i++];
		*p++ = font[i++];
		*p++ = 0;
		c = *str++;
	}
	while (x < xend) {
		*p++ = 0;
		x++;
	}
}

void LCD_drawStringInvN(int x, int line, const char *str, int xend) {
	if ((line < 0) || (line >= (LCDROWS)) || (x < 0))
		return;
	unsigned char c = *str++;
	uint8_t *p = &lcd_buffer[LCDHEADER + x + line * (LCDHEADER + LCDWIDTH)];
	*p++ = 0xFF;
	x++;
	while (c) {
		if (x + 6 >= xend)
			break;
		x += 6;
		int i = c * 5;
		*p++ = ~font[i++];
		*p++ = ~font[i++];
		*p++ = ~font[i++];
		*p++ = ~font[i++];
		*p++ = ~font[i++];
		*p++ = 0xFF;
		c = *str++;
	}
	while (x < xend) {
		*p++ = 0xFF;
		x++;
	}
}

void LCD_drawIBAR(int x, int line, int v, int N) {
	if ((line < 0) || (line >= (LCDHEIGHT / 8)) || (x < 0))
		return;
	int j = LCDHEADER + x + (line * LCDWIDTH);
	int k = 1;
	int i;
	if (v > 0) {
		for (i = 0; i < 30; i++) {
			x++;
			lcd_buffer[j++] = (v > k) << 1;
			k = k << 1;
		}
	} else {
		v = -v;
		for (i = 0; i < 30; i++) {
			x++;
			lcd_buffer[j++] = (v > k) << 6;
			k = k << 1;
		}
	}
	while (x < N) {
		lcd_buffer[j++] = 0;
		x++;
	}
}

void LCD_drawIBARadd(int x, int line, int v) {
	if ((line < 0) || (line >= (LCDHEIGHT)) || (x < 0))
		return;
	int j = LCDHEADER + x + (line * LCDWIDTH);
	int b = 1 << (line & 0x07);
	if (v + x > LCDWIDTH) { // clip
		v = LCDWIDTH - x;
	}
	int i;
	if (v > 0) {
		for (i = 0; i < v; i++) {
			x++;
			lcd_buffer[j] &= ~b;
			lcd_buffer[j++] += b;
		}
	}
}
