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
#include "glcdfont.h"
#include <string.h>

#define VALIDATE_ARGS 1

uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS] SRAM2;
led_outputs_t leds[LEDSIZE] SRAM2;

void axoloti_control_init(void) {
	LCD_clear();
	LED_clear(LED_RING_LEFT);
	LED_clear(LED_RING_RIGHT);
	LED_clear(LED_STEPS);
}

#define _BV(bit) (1 << (bit))

void LED_clear(led_outputs_t *c) {
	c->led_32b = 0;
}

void LED_set(led_outputs_t *c, int32_t v) {
	c->led_32b = v;
}

void LED_setOne(led_outputs_t *c, unsigned b) {
	int v = 0x3 << (b * 2);
	c->led_32b = v;
}

void LED_addOne(led_outputs_t *c, unsigned b, unsigned v) {
	int x = v << (b * 2);
	c->led_32b &= ~x;
	c->led_32b |= x;
}

// the most basic function, set a single pixel
void LCD_drawPixel(int x, int y, uint16_t color) {
#if VALIDATE_ARGS
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return;
#endif
	// x is which column
	if (color)
		lcd_buffer[LCDHEADER + x + (y / 8) * LCDWIDTH] |= _BV(y % 8);
	else
		lcd_buffer[LCDHEADER + x + (y / 8) * LCDWIDTH] &= ~_BV(y % 8);
}

void LCD_setPixel(int x, int y) {
#if VALIDATE_ARGS
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return;
#endif
	lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)] |= _BV(y % 8);
}

void LCD_clearPixel(int x, int y) {
#if VALIDATE_ARGS
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return;
#endif
	lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)] &= ~_BV(y % 8);
}

uint8_t LCD_getPixel(int x, int y) {
#if VALIDATE_ARGS
	if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
		return 0;
#endif
	return (lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)]
			>> (y % 8)) & 0x1;
}

// clear everything
void LCD_clear(void) {
	int i;
	for (i = 0; i < LCDROWS; i++) {
		uint32_t *p = (uint32_t *)&lcd_buffer[LCDHEADER + i * LCDWIDTH];
		int j = LCDWIDTH/4;
		while(j--) {
			*p++=0;
		}
	}
}

void LCD_grey(void) {
	int i;
	for (i = 0; i < LCDROWS; i++) {
		uint32_t *p = (uint32_t *)&lcd_buffer[LCDHEADER + i * LCDWIDTH];
		int j = LCDWIDTH/4;
		while(j--) {
			*p++=0b10101010010101011010101001010101;
		}
	}
}

void LCD_drawChar(int x, int line, unsigned char c) {
#if VALIDATE_ARGS
	if ((x < 0) || (x >= (LCDWIDTH - 5)) || (line < 0) || (line >= (LCDROWS)))
		return;
#endif
	int j = LCDHEADER + x + line * (LCDWIDTH + LCDHEADER);
	uint8_t *p = &lcd_buffer[j];
	const uint8_t *f = &font[c][0];
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f;
}

void LCD_drawCharInv(int x, int line, unsigned char c) {
#if VALIDATE_ARGS
	if ((x < 0) || (x >= (LCDWIDTH - 5)) || (line < 0)
			|| (line >= (LCDHEIGHT / 8)))
		return;
#endif
	int j = LCDHEADER + x + line * (LCDHEADER + LCDWIDTH);
	uint8_t *p = &lcd_buffer[j];
	const uint8_t *f = &font[c][0];
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f;
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
#if VALIDATE_ARGS
	if ((line < 0) || (line >= LCDROWS) || (x < 0))
		return;
#endif
	unsigned char c;
	int j = LCDHEADER + x + line * (LCDWIDTH + LCDHEADER);
	uint8_t *p = &lcd_buffer[j];
	*p++ = 0x00;
	int x2 = LCDWIDTH - x;
	while ((c = *str++)) {
		x2 -= 6;
		if (x2 <= 0) return;
		const uint8_t *f = font[c];
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
	}
}

void LCD_drawStringInv(int x, int line, const char *str) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= LCDROWS) || (x < 0))
		return;
#endif
	unsigned char c;
	int j = LCDHEADER + x + line * (LCDHEADER + LCDWIDTH);
	uint8_t *p = &lcd_buffer[j];
	*p++ = 0xFF;
	int x2 = LCDWIDTH - x;
	while ((c = *str++)) {
		x2 -= 6;
		if (x2 <= 0) return;
		const uint8_t *f = font[c];
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
	}
}

void LCD_drawStringN(int x, int line, const char *str, int xend) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDROWS)) || (x < 0))
		return;
#endif
	unsigned char c = *str++;
	uint8_t *p = &lcd_buffer[LCDHEADER + x + line * (LCDHEADER + LCDWIDTH)];
	*p++ = 0;
	x++;
	while (c) {
		if (x + 6 >= xend)
			break;
		x += 6;
		const uint8_t *f = font[c];
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f;
		c = *str++;
	}
	while (x < xend) {
		*p++ = 0;
		x++;
	}
}

void LCD_drawStringInvN(int x, int line, const char *str, int xend) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDROWS)) || (x < 0))
		return;
#endif
	unsigned char c = *str++;
	uint8_t *p = &lcd_buffer[LCDHEADER + x + line * (LCDHEADER + LCDWIDTH)];
	*p++ = 0xFF;
	x++;
	while (c) {
		if (x + 6 >= xend)
			break;
		x += 6;
		const uint8_t *f = font[c];
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f;
		c = *str++;
	}
	while (x < xend) {
		*p++ = 0xFF;
		x++;
	}
}

void LCD_drawIBAR(int x, int line, int v, int N) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDHEIGHT / 8)) || (x < 0))
		return;
#endif
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
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDHEIGHT)) || (x < 0))
		return;
#endif
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
