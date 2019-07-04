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
#define LCDHEADER 0 // probably broken for other values than 0
#define LCDROWS (LCDHEIGHT/8)
#define LCD_CHAR_WIDTH 6

// leds, dont exceed SPILINK_CTLDATASIZE
#define LEDSIZE 6

#include "inttypes.h"

extern uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS];

struct led_array {
	union {
		struct {
		  unsigned int led1a :1;
		  unsigned int led2a :1;
		  unsigned int led3a :1;
		  unsigned int led4a :1;
		  unsigned int led5a :1;
		  unsigned int led6a :1;
		  unsigned int led7a :1;
		  unsigned int led8a :1;
		  unsigned int led9a :1;
		  unsigned int led10a :1;
		  unsigned int led11a :1;
		  unsigned int led12a :1;
		  unsigned int led13a :1;
		  unsigned int led14a :1;
		  unsigned int led15a :1;
		  unsigned int led16a :1;
		  unsigned int led1b :1;
		  unsigned int led2b :1;
		  unsigned int led3b :1;
		  unsigned int led4b :1;
		  unsigned int led5b :1;
		  unsigned int led6b :1;
		  unsigned int led7b :1;
		  unsigned int led8b :1;
		  unsigned int led9b :1;
		  unsigned int led10b :1;
		  unsigned int led11b :1;
		  unsigned int led12b :1;
		  unsigned int led13b :1;
		  unsigned int led14b :1;
		  unsigned int led15b :1;
		  unsigned int led16b :1;
		};
		uint32_t led_32b;
	};
};

typedef struct led_array led_array_t;

extern led_array_t leds[LEDSIZE];
#define LED_RING_TOPLEFT (&leds[0])
#define LED_RING_TOPRIGHT (&leds[1])
#define LED_RING_BOTTOMLEFT (&leds[2])
#define LED_RING_BOTTOMRIGHT (&leds[3])
#define LED_STEPS (&leds[4])
#define LED_LVL (&leds[5])

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
