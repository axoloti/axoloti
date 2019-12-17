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
// todo: fix VALIDATE_ARG checks

uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS] DMA_MEM_FW;
led_array_t leds[LEDSIZE] DMA_MEM_FW;

void axoloti_control_init(void) {
	LCD_clear();
	LED_clear(LED_RING_TOPLEFT);
	LED_clear(LED_RING_TOPRIGHT);
	LED_clear(LED_RING_BOTTOMLEFT);
	LED_clear(LED_RING_BOTTOMRIGHT);
	LED_clear(LED_STEPS);
}

#define _BV(bit) (1 << (bit))

void LED_clear(led_array_t *c) {
	c->led_32b = 0;
}

void LED_set(led_array_t *c, int32_t v) {
	c->led_32b = v;
}

void LED_setOne(led_array_t *c, unsigned b) {
	int v = 0x10001 << b;
	c->led_32b = v;
}

void LED_addOne(led_array_t *c, unsigned b, unsigned v) {
	int x = (v+(v<<16)) << b;
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
		uint32_t *p = (uint32_t *) &lcd_buffer[LCDHEADER + i * LCDWIDTH];
		int j = LCDWIDTH / 4;
		while (j--) {
			*p++ = 0;
		}
	}
}

void LCD_grey(void) {
	int i;
	for (i = 0; i < LCDROWS; i++) {
		uint32_t *p = (uint32_t *) &lcd_buffer[LCDHEADER + i * LCDWIDTH];
		int j = LCDWIDTH / 4;
		while (j--) {
			*p++ = 0b10101010010101011010101001010101;
		}
	}
}

__STATIC_INLINE uint16_t * LCD_position_to_pointer(int halfx, int line) {
	return (uint16_t *) &lcd_buffer[LCDHEADER + (halfx * 2)
			+ line * (LCDWIDTH + LCDHEADER)];
}

void LCD_drawChar(int half_x, int line, unsigned char c) {
#if VALIDATE_ARGS
	if ((half_x < 0) || (half_x >= (LCDWIDTH - 5)) || (line < 0)
			|| (line >= (LCDROWS)))
		return;
#endif
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	const uint16_t *f = (uint16_t *) &font[c][0];
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f;
}

void LCD_drawCharInv(int half_x, int line, unsigned char c) {
#if VALIDATE_ARGS
	if ((half_x < 0) || (half_x >= (LCDWIDTH - 5)) || (line < 0)
			|| (line >= (LCDHEIGHT / 8)))
		return;
#endif
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	const uint16_t *f = (uint16_t *) &font[c][0];
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f;
}

uint16_t * LCD_drawChar_ll(uint16_t *p, unsigned char c) {
	const uint16_t *f = (uint16_t *) &font[c][0];
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f;
	return p;
}

uint16_t * LCD_drawCharInv_ll(uint16_t *p, unsigned char c) {
	const uint16_t *f = (uint16_t *) &font[c][0];
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f;
	return p;
}

uint16_t * LCD_drawDigit_ll(uint16_t *p, unsigned char d) {
	const uint16_t *f = (uint16_t *) &font['0' + d][0];
	*p++ = *f++;
	*p++ = *f++;
	*p++ = *f;
	return p;
}

uint16_t * LCD_drawDigitInv_ll(uint16_t *p, unsigned char d) {
	const uint16_t *f = (uint16_t *) &font['0' + d][0];
	*p++ = ~*f++;
	*p++ = ~*f++;
	*p++ = ~*f;
	return p;
}

void LCD_drawNumber3D(int half_x, int line, int value) {
	// draw sign and 3 decimal digits of an integer
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	if (value < 0) {
		p = LCD_drawChar_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawChar_ll(p, ' ');
	int j;
	bool nzero = 0;
	j = value / 100;
	if (j < 10) {
		nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*100;
		j = value/10;      nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*10;
		p = LCD_drawDigit_ll(p, value);
	} else {
		p = LCD_drawChar_ll(p, 'o');
		p = LCD_drawChar_ll(p, 'v');
		p = LCD_drawChar_ll(p, 'r');
	}
	*p = 0;
}

void LCD_drawNumber3DInv(int half_x, int line, int value) {
	// draw sign and 3 decimal digits of an integer
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	if (value < 0) {
		p = LCD_drawCharInv_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawCharInv_ll(p, ' ');
	int j;
	bool nzero = 0;
	j = value / 100;
	if (j < 10) {
		nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*100;
		j = value/10;      nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*10;
		p = LCD_drawDigitInv_ll(p, value);
	} else {
		p = LCD_drawCharInv_ll(p, 'o');
		p = LCD_drawCharInv_ll(p, 'v');
		p = LCD_drawCharInv_ll(p, 'r');
	}
	*p = ~0;
}

void LCD_drawNumber5D(int half_x, int line, int value) {
	// draw sign and 5 decimal digits of an integer
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	if (value < 0) {
		p = LCD_drawChar_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawChar_ll(p, ' ');
	int j;
	bool nzero = 0;
	j = value / 10000;
	if (j < 10) {
		nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*10000;
		j = value/1000;    nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*1000;
		j = value/100;     nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*100;
		j = value/10;      nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*10;
		p = LCD_drawDigit_ll(p, value);
	} else {
		p = LCD_drawChar_ll(p, 'o');
		p = LCD_drawChar_ll(p, 'v');
		p = LCD_drawChar_ll(p, 'r');
		p = LCD_drawChar_ll(p, 'f');
		p = LCD_drawChar_ll(p, 'l');
	}
	*p = 0;
}

void LCD_drawNumber5DInv(int half_x, int line, int value) {
	// draw sign and 5 decimal digits of an integer
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	if (value < 0) {
		p = LCD_drawCharInv_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawCharInv_ll(p, ' ');
	int j;
	bool nzero = 0;
	j = value / 10000;
	if (j < 10) {
		nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*10000;
		j = value/1000;    nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*1000;
		j = value/100;     nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*100;
		j = value/10;      nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*10;
		p = LCD_drawDigitInv_ll(p, value);
	} else {
		p = LCD_drawCharInv_ll(p, 'o');
		p = LCD_drawCharInv_ll(p, 'v');
		p = LCD_drawCharInv_ll(p, 'r');
		p = LCD_drawCharInv_ll(p, 'f');
		p = LCD_drawCharInv_ll(p, 'l');
	}
	*p = ~0;
}

void LCD_drawNumber7D(int half_x, int line, int value) {
	// draw sign and 7 decimal digits of an integer
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	if (value < 0) {
		p = LCD_drawChar_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawChar_ll(p, ' ');
	int j;
	bool nzero = 0;
	j = value / 1000000;
	if (j < 10) {
		nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*1000000;
		j = value/100000;  nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*100000;
		j = value/10000;   nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*10000;
		j = value/1000;    nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*1000;
		j = value/100;     nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*100;
		j = value/10;      nzero = nzero||j; if (nzero) p = LCD_drawDigit_ll(p, j); else p = LCD_drawChar_ll(p, ' '); value -= j*10;
		p = LCD_drawDigit_ll(p, value);
	} else {
		p = LCD_drawChar_ll(p, 'o');
		p = LCD_drawChar_ll(p, 'v');
		p = LCD_drawChar_ll(p, 'r');
		p = LCD_drawChar_ll(p, 'f');
		p = LCD_drawChar_ll(p, 'l');
		p = LCD_drawChar_ll(p, 'o');
		p = LCD_drawChar_ll(p, 'w');
	}
	*p = 0;
}

void LCD_drawNumber7DInv(int half_x, int line, int value) {
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	if (value < 0) {
		p = LCD_drawCharInv_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawCharInv_ll(p, ' ');
	int j;
	bool nzero = 0;
	j = value / 1000000;
	if (j < 10) {
		nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*1000000;
		j = value/100000;  nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*100000;
		j = value/10000;   nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*10000;
		j = value/1000;    nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*1000;
		j = value/100;     nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*100;
		j = value/10;      nzero = nzero||j; if (nzero) p = LCD_drawDigitInv_ll(p, j); else p = LCD_drawCharInv_ll(p, ' '); value -= j*10;
		p = LCD_drawDigitInv_ll(p, value);
	} else {
		p = LCD_drawCharInv_ll(p, 'o');
		p = LCD_drawCharInv_ll(p, 'v');
		p = LCD_drawCharInv_ll(p, 'r');
		p = LCD_drawCharInv_ll(p, 'f');
		p = LCD_drawCharInv_ll(p, 'l');
		p = LCD_drawCharInv_ll(p, 'o');
		p = LCD_drawCharInv_ll(p, 'w');
	}
	*p = ~0;
}

__STATIC_INLINE char hexchar_from_nibble(int x) {
	return (x < 10 ? '0' + x : 'A' - 10 + x);
}

void LCD_drawNumberHex32(int half_x, int line, uint32_t value) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDHEIGHT)) || (half_x < 0)
			|| (half_x > (LCDWIDTH - 48)))
		return;
#endif
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = 0;
	char d;
	d = (value >> 28) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value >> 24) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value >> 20) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value >> 16) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value >> 12) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value >> 8) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value >> 4) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
	d = (value) & 0xF;
	p = LCD_drawChar_ll(p, hexchar_from_nibble(d));
}

void LCD_drawNumberHex32Inv(int half_x, int line, uint32_t value) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDHEIGHT)) || (half_x < 0)
			|| (half_x > (LCDWIDTH - 48)))
		return;
#endif
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = ~0;
	char d;
	d = (value >> 28) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value >> 24) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value >> 20) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value >> 16) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value >> 12) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value >> 8) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value >> 4) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
	d = (value) & 0xF;
	p = LCD_drawCharInv_ll(p, hexchar_from_nibble(d));
}

void LCD_drawNumberQ27x64(int half_x, int line, int32_t value) {
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = 0;
	if (value < 0) {
		p = LCD_drawChar_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawChar_ll(p, ' ');
	int j;
	bool nzero = 0;
	int value1 = value >> 21;
	j = value1 / 100;
	if (j < 10) {
		nzero = nzero || j;
		if (nzero)
			p = LCD_drawDigit_ll(p, j);
		else
			p = LCD_drawChar_ll(p, ' ');
		value1 -= j * 100;
		j = value1 / 10;
		nzero = nzero || j;
		if (nzero)
			p = LCD_drawDigit_ll(p, j);
		else
			p = LCD_drawChar_ll(p, ' ');
		value1 -= j * 10;
		j = value1;
		p = LCD_drawDigit_ll(p, j);
		int value2 = ((value & 0x01FFFFF) * 999 + 0x0100000) >> 21;
		p = LCD_drawChar_ll(p, '.');
		j = value2 / 100;
		p = LCD_drawDigit_ll(p, j);
		value2 -= j * 100;
		j = value2 / 10;
		p = LCD_drawDigit_ll(p, j);
		value2 -= j * 10;
		p = LCD_drawDigit_ll(p, value2);
	} else {
		p = LCD_drawChar_ll(p, 'o');
		p = LCD_drawChar_ll(p, 'v');
		p = LCD_drawChar_ll(p, 'r');
		p = LCD_drawChar_ll(p, 'f');
		p = LCD_drawChar_ll(p, 'l');
		p = LCD_drawChar_ll(p, 'o');
		p = LCD_drawChar_ll(p, 'w');
	}
}

void LCD_drawNumberQ27x64Inv(int half_x, int line, int32_t value) {
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = ~0;
	if (value < 0) {
		p = LCD_drawCharInv_ll(p, '-');
		value = -value;
	} else
		p = LCD_drawCharInv_ll(p, ' ');
	int j;
	bool nzero = 0;
	int value1 = value >> 21;
	j = value1 / 100;
	if (j < 10) {
		nzero = nzero || j;
		if (nzero)
			p = LCD_drawDigitInv_ll(p, j);
		else
			p = LCD_drawCharInv_ll(p, ' ');
		value1 -= j * 100;
		j = value1 / 10;
		nzero = nzero || j;
		if (nzero)
			p = LCD_drawDigitInv_ll(p, j);
		else
			p = LCD_drawCharInv_ll(p, ' ');
		value1 -= j * 10;
		j = value1;
		p = LCD_drawDigitInv_ll(p, j);
		int value2 = ((value & 0x01FFFFF) * 1000 + 0x0100000) >> 21;
		p = LCD_drawCharInv_ll(p, '.');
		j = value2 / 100;
		p = LCD_drawDigitInv_ll(p, j);
		value2 -= j * 100;
		j = value2 / 10;
		p = LCD_drawDigitInv_ll(p, j);
		value2 -= j * 10;
		p = LCD_drawDigitInv_ll(p, value2);
	} else {
		p = LCD_drawCharInv_ll(p, 'o');
		p = LCD_drawCharInv_ll(p, 'v');
		p = LCD_drawCharInv_ll(p, 'r');
		p = LCD_drawCharInv_ll(p, 'f');
		p = LCD_drawCharInv_ll(p, 'l');
		p = LCD_drawCharInv_ll(p, 'o');
		p = LCD_drawCharInv_ll(p, 'w');
	}
}

void LCD_drawString(int half_x, int line, const char *str) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= LCDROWS) || (half_x < 0))
		return;
#endif
	unsigned char c;
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = 0x00;
	int half_x2 = LCDWIDTH - half_x;
	while ((c = *str++)) {
		half_x2 -= 6;
		if (half_x2 <= 0)
			return;
		const uint16_t *f = (uint16_t *) font[c];
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f++;
	}
}

void LCD_drawStringInv(int half_x, int line, const char *str) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= LCDROWS) || (half_x < 0))
		return;
#endif
	unsigned char c;
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = 0xFF00;
	int half_x2 = LCDWIDTH - half_x;
	while ((c = *str++)) {
		half_x2 -= 6;
		if (half_x2 <= 0)
			return;
		const uint16_t *f = (uint16_t *) font[c];
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f++;
	}
}

void LCD_drawStringN(int half_x, int line, const char *str, int n) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDROWS)) || (half_x < 0))
		return;
#endif
	unsigned char c = *str++;
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = 0;
	int ci = n;
	while (c && ci) {
		const uint16_t *f = (uint16_t *) font[c];
		*p++ = *f++;
		*p++ = *f++;
		*p++ = *f;
		c = *str++;
		ci--;
	}
	while (ci--) {
		*p++ = 0;
		*p++ = 0;
		*p++ = 0;
	}
}

void LCD_drawStringInvN(int half_x, int line, const char *str, int n) {
#if VALIDATE_ARGS
	if ((line < 0) || (line >= (LCDROWS)) || (half_x < 0))
		return;
#endif
	unsigned char c = *str++;
	uint16_t *p = LCD_position_to_pointer(half_x, line);
	*p++ = ~0;
	int ci = n;
	while (c && ci) {
		const uint16_t *f = (uint16_t *) font[c];
		*p++ = ~*f++;
		*p++ = ~*f++;
		*p++ = ~*f;
		c = *str++;
		ci--;
	}
	while (ci--) {
		*p++ = ~0;
		*p++ = ~0;
		*p++ = ~0;
	}
}

void LCD_drawBitField(int half_x, int line, int value, int nbits) {
	// 49 pixels wide
	// 1 pixels per bit
	// for up to 32 bits
	uint8_t *p = (uint8_t *) LCD_position_to_pointer(half_x, line);
	*p++ = 0;
	*p++ = 0b01111110;
	int b = nbits;
	while (b--) {
		*p++ = (value & 1) ? 0b01011010 : 0b01000010;
		value = value >> 1;
	}
	*p++ = 0b01111110;
	*p++ = 0;
	b = 49 - nbits - 4;
	while (b-- > 0) {
		*p++ = 0;
	}
}

void LCD_drawBitFieldInv(int half_x, int line, int value, int nbits) {
	// 49 pixels wide
	// 1 pixels per bit
	// for up to 32 bits
	uint8_t *p = (uint8_t *) LCD_position_to_pointer(half_x, line);
	*p++ = ~0;
	*p++ = ~0b01000010;
	int b = nbits;
	while (b--) {
		int v = (value & 1) ? ~0b01100110 : ~0b01111110;
		*p++ = v;
		value = value >> 1;
	}
	*p++ = ~0b01000010;
	*p++ = ~0;
	b = 49 - nbits - 4;
	while (b-- > 0) {
		*p++ = ~0;
	}
}

void LCD_drawBitField2(int half_x, int line, int value, int nbits) {
	// 49 pixels wide
	// 2 pixels per bit
	// for 21 bits or less
	uint8_t *p = (uint8_t *) LCD_position_to_pointer(half_x, line);
	*p++ = 0;
	int b = nbits;
	while (b--) {
		int v = (value & 1) ? 0b01011010 : 0b01000010;
		*p++ = v;
		*p++ = v;
		value = value >> 1;
	}
	*p++ = 0;
	b = 49 - nbits * 2 - 2;
	while (b-- > 0) {
		*p++ = 0;
	}
}

void LCD_drawBitField2Inv(int half_x, int line, int value, int nbits) {
	// 49 pixels wide
	// 2 pixels per bit
	// for 21 bits or less
	uint8_t *p = (uint8_t *) LCD_position_to_pointer(half_x, line);
	*p++ = ~0;
	int b = nbits;
	while (b--) {
		int v = (value & 1) ? ~0b01100110 : ~0b01111110;
		*p++ = v;
		*p++ = v;
		value = value >> 1;
	}
	*p++ = ~0;
	b = 49 - nbits * 2 - 2;
	while (b-- > 0) {
		*p++ = ~0;
	}
}

void LCD_drawHBar(int half_x, int line, int value, int length) {
	// 49 pixels wide
	// 1 pixels per bit
	// for length up to 45
	uint8_t *p = (uint8_t *) LCD_position_to_pointer(half_x, line);
	if (value < 0)
		value = 0;
	if (value >= length)
		value = length - 1;
	int margin = 49 - length - 2;

	*p++ = 0;
	int b = margin / 2;
	while (b-- > 0) {
		*p++ = 0;
	}
	b = value;
	while (b--) {
		*p++ = 0b01011010;
	}
	b = length - value;
	while (b--) {
		*p++ = 0b01000010;
	}
	*p++ = 0;
	b = 49 - margin / 2 - length;
	while (b-- > 0) {
		*p++ = 0;
	}
}

void LCD_drawHBarInv(int half_x, int line, int value, int length) {
	// 49 pixels wide
	// 1 pixels per bit
	// for length up to 45
	// note: only the bounding box is inverted
	uint8_t *p = (uint8_t *) LCD_position_to_pointer(half_x, line);
	if (value < 0)
		value = 0;
	if (value >= length)
		value = 0;
	*p++ = ~0;
	*p++ = ~0;
	int b = value;
	b = value;
	while (b-- > 0) {
		*p++ = ~0b01111110;
	}
	*p++ = ~0b01100110;
	b = length - value - 1;
	while (b--) {
		*p++ = ~0b01111110;
	}
	*p++ = ~0;
	*p++ = ~0;
	b = 49 - length - 4;
	while (b-- > 0) {
		*p++ = ~0;
	}
}


/*
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
*/
