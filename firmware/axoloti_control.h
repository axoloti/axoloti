/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
#ifndef __AXOLOTI_CONTROL_H
#define __AXOLOTI_CONTROL_H

#define LCDWIDTH 128
#define LCDHEIGHT 64
#define LCDHEADER 0
#define LCDROWS (LCDHEIGHT/8)


// leds, dont exceed SPILINK_CTLDATASIZE
#define LEDSIZE 3

extern uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS];

typedef struct led_outputs {
	union {
		struct {
		  unsigned int led1 :2;
		  unsigned int led2 :2;
		  unsigned int led3 :2;
		  unsigned int led4 :2;
		  unsigned int led5 :2;
		  unsigned int led6 :2;
		  unsigned int led7 :2;
		  unsigned int led8 :2;
		  unsigned int led9 :2;
		  unsigned int led10 :2;
		  unsigned int led11 :2;
		  unsigned int led12 :2;
		  unsigned int led13 :2;
		  unsigned int led14 :2;
		  unsigned int led15 :2;
		  unsigned int led16 :2;
		};
		uint32_t led_32b;
	};
} led_outputs_t;

extern led_outputs_t leds[LEDSIZE];

extern uint8_t control_rx_buffer[(LCDHEADER + LCDWIDTH)];

extern void do_axoloti_control(void);
void axoloti_control_init(void);

void LED_clear(void);
void LED_setAll(unsigned c, int32_t v);
void LED_setOne(unsigned c, unsigned b, unsigned v );

void LCD_clearDisplay(void);
void LCD_drawPixel(int x, int y, uint16_t color);
void LCD_setPixel(int x, int y);
void LCD_clearPixel(int x, int y);
uint8_t LCD_getPixel(int x, int y);
void LCD_drawChar(int x, int y, unsigned char c);
void LCD_drawCharInv(int x, int y, unsigned char c);
void LCD_drawNumber3D(int x, int y, int i);
void LCD_drawNumber3DInv(int x, int y, int i);
void LCD_drawNumber5D(int x, int y, int i);
void LCD_drawNumber5DInv(int x, int y, int i);
void LCD_drawString(int x, int y, const char *str);
void LCD_drawStringInv(int x, int y, const char *str);
void LCD_drawStringN(int x, int y, const char *str, int N);
void LCD_drawStringInvN(int x, int y, const char *str, int N);
void LCD_drawIBAR(int x, int y, int v, int N);
void LCD_drawIBARInv(int x, int y, int v, int N);
void LCD_drawIBARadd(int x, int y, int v);
void LCD_drawHex32(int x, int y, uint32_t i);
#endif
