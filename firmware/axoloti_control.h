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
#ifndef __AXOLOTI_CONTROL_H
#define __AXOLOTI_CONTROL_H

#define LCDWIDTH 128
#define LCDHEIGHT 64
#define LCDHEADER 0
#define LCDROWS (LCDHEIGHT/8)
#define LCD_CHAR_WIDTH 6

// leds, dont exceed SPILINK_CTLDATASIZE
#define LEDSIZE 3

extern uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS];

typedef struct {
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
} led_array_t;

extern led_array_t leds[LEDSIZE];
#define LED_RING_LEFT (&leds[0])
#define LED_RING_RIGHT (&leds[1])
#define LED_STEPS (&leds[2])

extern uint8_t control_rx_buffer[(LCDHEADER + LCDWIDTH)];

extern void do_axoloti_control(void);
void axoloti_control_init(void);

void LED_clear(led_array_t *c);
void LED_set(led_array_t *c, int32_t v);
void LED_setOne(led_array_t *c, unsigned v);
void LED_addOne(led_array_t *c, unsigned b, unsigned v );

void LCD_clear(void);
void LCD_grey(void);

void LCD_drawPixel(int x, int y, uint16_t color);
void LCD_setPixel(int x, int y);
void LCD_clearPixel(int x, int y);
uint8_t LCD_getPixel(int x, int y);

void LCD_drawChar(int half_x, int line, unsigned char c);
void LCD_drawCharInv(int half_x, int line, unsigned char c);

void LCD_drawNumber7D(int half_x, int line, int value);
void LCD_drawNumber7DInv(int half_x, int line, int value);

void LCD_drawNumberHex32(int half_x, int line, uint32_t value);
void LCD_drawNumberHex32Inv(int half_x, int line, uint32_t value);

void LCD_drawNumberQ27x64(int half_x, int line, int32_t value);
void LCD_drawNumberQ27x64Inv(int half_x, int line, int32_t value);

void LCD_drawString(int half_x, int line, const char *str);
void LCD_drawStringInv(int half_x, int line, const char *str);
void LCD_drawStringN(int half_x, int line, const char *str, int N);
void LCD_drawStringInvN(int half_x, int line, const char *str, int N);

void LCD_drawBitField(int half_x, int line, int value, int nbits);
void LCD_drawBitFieldInv(int half_x, int line, int value, int nbits);
void LCD_drawBitField2(int half_x, int line, int value, int nbits);
void LCD_drawBitField2Inv(int half_x, int line, int value, int nbits);
void LCD_drawHBar(int half_x, int line, int value, int length);
void LCD_drawHBarInv(int half_x, int line, int value, int length);

// obsolete?
void LCD_drawIBAR(int half_x, int y, int v, int N);
void LCD_drawIBARInv(int half_x, int y, int v, int N);
void LCD_drawIBARadd(int half_x, int y, int v);

void LCD_drawNumber3D(int half_x, int line, int value);
void LCD_drawNumber3DInv(int half_x, int line, int value);
void LCD_drawNumber5D(int half_x, int line, int value);
void LCD_drawNumber5DInv(int half_x, int line, int value);


#endif
