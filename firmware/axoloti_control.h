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
#define LCDHEADER 4
#define LCDROWS (LCDHEIGHT/8)

extern uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS];
extern uint8_t led_buffer[LCDHEADER + LCDWIDTH];
extern uint8_t control_rx_buffer[(LCDHEADER + LCDWIDTH)];

extern void do_axoloti_control(void);
void axoloti_control_init(void);

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

#endif
