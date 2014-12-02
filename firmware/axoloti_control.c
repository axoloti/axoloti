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
#include "ch.h"
#include "hal.h"
#include "axoloti_control.h"
#include "axoloti_board.h"
#include "ui.h"
#include <string.h>

uint8_t lcd_buffer[(LCDHEADER + LCDWIDTH) * LCDROWS] __attribute__ ((section (".sram2")));
uint8_t led_buffer[LCDHEADER + LCDWIDTH] __attribute__ ((section (".sram2")));
uint8_t control_rx_buffer[LCDHEADER + LCDWIDTH] __attribute__ ((section (".sram2")));

/*
 * Low speed SPI configuration (328.125kHz, CPHA=0, CPOL=0, MSb first).
 */
static const SPIConfig ls_spicfg =
    {NULL, GPIOA, 15, SPI_CR1_BR_0 | SPI_CR1_BR_1};

uint8_t row_update_index;
uint8_t k;

void do_axoloti_control(void) {
  row_update_index++;
  if (row_update_index == ((LCDROWS) + 1)) {
    row_update_index = 0;
    k++;
  }
  chMtxLock(&Mutex_DMAStream_1_7);
  spiAcquireBus(&SPID3); /* Acquire ownership of the bus.    */
  spiStart(&SPID3, &ls_spicfg); /* Setup transfer parameters.       */
  spiSelect(&SPID3); /* Slave Select assertion.          */
  chThdSleepMilliseconds(2);
  if (row_update_index != (LCDROWS))
    spiExchange(&SPID3, LCDHEADER + LCDWIDTH,
                &lcd_buffer[(LCDHEADER + LCDWIDTH) * row_update_index],
                control_rx_buffer);
  else
    spiExchange(&SPID3, LCDHEADER + LCDWIDTH, &led_buffer[0],
                control_rx_buffer);
  spiUnselect(&SPID3); /* Slave Select de-assertion.       */
  spiReleaseBus(&SPID3); /* Ownership release.               */
  spiStop(&SPID3);
  chMtxUnlock();
  if ((control_rx_buffer[0] == 'B') && (control_rx_buffer[1] == 'T')
      && (control_rx_buffer[2] == 'N')) {
    Btn_Nav_Or.word |= ((int32_t *)control_rx_buffer)[1];
    Btn_Nav_And.word &= ((int32_t *)control_rx_buffer)[2];
    EncBuffer[0] += control_rx_buffer[12];
    EncBuffer[1] += control_rx_buffer[13];
    EncBuffer[2] += control_rx_buffer[14];
    EncBuffer[3] += control_rx_buffer[15];
  }
}

void axoloti_control_init(void) {
  /*
   *  Commm to FP test...
   */
  row_update_index = 0;
  palSetPadMode(GPIOA, 15, PAL_MODE_OUTPUT_PUSHPULL);
  // NSS
  palSetPadMode(GPIOB, 5, PAL_MODE_ALTERNATE(6));
  // MOSI
  palSetPadMode(GPIOB, 4, PAL_MODE_ALTERNATE(6));
  // MISO
  palSetPadMode(GPIOB, 3, PAL_MODE_ALTERNATE(6));
  // SCK
  int i;
  // clear
  for (i = 0; i < (LCDHEADER + LCDWIDTH) * LCDROWS; i++)
    lcd_buffer[i] = 0;
  // fill header
  for (i = 0; i < LCDROWS; i++) {
    lcd_buffer[i * (LCDHEADER + LCDWIDTH) + 0] = 'A';
    lcd_buffer[i * (LCDHEADER + LCDWIDTH) + 1] = 'x';
    lcd_buffer[i * (LCDHEADER + LCDWIDTH) + 2] = 'o';
    lcd_buffer[i * (LCDHEADER + LCDWIDTH) + 3] = '0' + i;
  }
  led_buffer[0] = 'A';
  led_buffer[1] = 'x';
  led_buffer[2] = 'o';
  led_buffer[3] = '0' + i;

  for (i = 0; i < LCDWIDTH; i++)
    led_buffer[LCDHEADER + i] = 0; //i;

  for (i = 0; i < LCDWIDTH; i++)
    control_rx_buffer[i] = 0;
}

#define _BV(bit) (1 << (bit))

void LCD_updateBoundingBox(int x, int y, int x2, int y2) {
  (void)x;
  (void)y;
  (void)x2;
  (void)y2;
}

// the most basic function, set a single pixel
void LCD_drawPixel(int x, int y, uint16_t color) {
  if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
    return;

  // x is which column
  if (color)
    lcd_buffer[LCDHEADER + x + (y / 8) * LCDWIDTH] |= _BV(y%8);
  else
    lcd_buffer[LCDHEADER + x + (y / 8) * LCDWIDTH] &= ~_BV(y%8);

  LCD_updateBoundingBox(x, y, x, y);
}

void LCD_setPixel(int x, int y) {
  if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
    return;
  lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)] |= _BV(y%8);
  LCD_updateBoundingBox(x, y, x, y);
}

void LCD_clearPixel(int x, int y) {
  if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
    return;
  lcd_buffer[LCDHEADER + x + (y / 8) * (LCDWIDTH + LCDHEADER)] &= ~_BV(y%8);
  LCD_updateBoundingBox(x, y, x, y);
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
  LCD_updateBoundingBox(0, 0, LCDWIDTH - 1, LCDHEIGHT - 1);
//  cursor_y = cursor_x = 0;
}

extern const unsigned char font[];

// draw a character
void LCD_drawChar(int x, int y, unsigned char c) {
  // pixel x, line y
  //
  if ((x < 0) || (x >= (LCDWIDTH - 5)) || (y < 0) || (y >= (LCDROWS)))
    return;
  int i = c * 5;
  int j = LCDHEADER + x + y * (LCDWIDTH + LCDHEADER);
  lcd_buffer[j++] = font[i++];
  lcd_buffer[j++] = font[i++];
  lcd_buffer[j++] = font[i++];
  lcd_buffer[j++] = font[i++];
  lcd_buffer[j++] = font[i++];
  lcd_buffer[j] = 0;
}

void LCD_drawString(int x, int y, const char *str) {
  if ((y < 0) || (y >= (LCDROWS))|| (x<0))return;
  // pixel x, line y
      unsigned char c;
      int j = LCDHEADER + x+ y*(LCDWIDTH+LCDHEADER);
      lcd_buffer[j++] = 0x00;
      int x2 = x;
      while((c=*str++)) {
        if (x2 >= LCDWIDTH)
        return;
        x2 += 6;
        int i=c*5;
        lcd_buffer[j++] = font[i++];
        lcd_buffer[j++] = font[i++];
        lcd_buffer[j++] = font[i++];
        lcd_buffer[j++] = font[i++];
        lcd_buffer[j++] = font[i++];
        lcd_buffer[j++] = 0;
      }
    }

void LCD_drawNumber3D(int x, int y, int i) {
  if (i < 0) {
    LCD_drawChar(x, y, '-');
    i = -i;
  }
  else
    LCD_drawChar(x, y, ' ');

  LCD_drawChar(x + 18, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawChar(x + 12, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawChar(x + 6, y, '0' + i);
}

void LCD_drawNumber5D(int x, int y, int i) {
  if (i < 0) {
    LCD_drawChar(x, y, '-');
    i = -i;
  }
  else
    LCD_drawChar(x, y, ' ');

  LCD_drawChar(x + 30, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawChar(x + 24, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawChar(x + 18, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawChar(x + 12, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawChar(x + 6, y, '0' + i);
}

void LCD_drawCharInv(int x, int y, unsigned char c) {
  // pixel x, line y
  //
  if ((x < 0) || (x >= (LCDWIDTH - 5)) || (y < 0) || (y >= (LCDHEIGHT / 8)))
    return;
  int i = c * 5;
  int j = LCDHEADER + x + y * (LCDHEADER + LCDWIDTH);
  lcd_buffer[j++] = ~font[i++];
  lcd_buffer[j++] = ~font[i++];
  lcd_buffer[j++] = ~font[i++];
  lcd_buffer[j++] = ~font[i++];
  lcd_buffer[j++] = ~font[i++];
  lcd_buffer[j] = 0xFF;
}

void LCD_drawStringInv(int x, int y, const char *str) {
  if ((y < 0) || (y >= (LCDROWS))|| (x<0))return;
  // pixel x, line y
      unsigned char c;
      int j = LCDHEADER + x+ y*(LCDHEADER+LCDWIDTH);
      lcd_buffer[j++] = 0xFF;
      int x2 = x;
      while((c=*str++)) {
        if (x2 >= LCDWIDTH)
        return;
        x2 += 6;
        int i=c*5;
        lcd_buffer[j++] = ~font[i++];
        lcd_buffer[j++] = ~font[i++];
        lcd_buffer[j++] = ~font[i++];
        lcd_buffer[j++] = ~font[i++];
        lcd_buffer[j++] = ~font[i++];
        lcd_buffer[j++] = 0xFF;
      }
    }

void LCD_drawNumber3DInv(int x, int y, int i) {
  if (i < 0) {
    LCD_drawCharInv(x, y, '-');
    i = -i;
  }
  else
    LCD_drawCharInv(x, y, ' ');

  LCD_drawCharInv(x + 18, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawCharInv(x + 12, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawCharInv(x + 6, y, '0' + i);
}

void LCD_drawNumber5DInv(int x, int y, int i) {
  if (i < 0) {
    LCD_drawCharInv(x, y, '-');
    i = -i;
  }
  else
    LCD_drawCharInv(x, y, ' ');

  LCD_drawCharInv(x + 30, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawCharInv(x + 24, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawCharInv(x + 18, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawCharInv(x + 12, y, '0' + (i % 10));
  i = i / 10;
  LCD_drawCharInv(x + 6, y, '0' + i);
}

void LCD_drawStringN(int x, int y, const char *str, int N) {
  (void)N;
  if ((y < 0) || (y >= (LCDROWS))|| (x<0))return;
  unsigned char c;
  lcd_buffer[LCDHEADER + x + y * (LCDHEADER + LCDWIDTH)] = 0x00;
  x++;
  while ((c = *str++)) {
    if (x >= (LCDWIDTH - 5))
      break;
    int i = c * 5;
    int j = LCDHEADER + x + y * (LCDHEADER + LCDWIDTH);
    lcd_buffer[j++] = font[i++];
    lcd_buffer[j++] = font[i++];
    lcd_buffer[j++] = font[i++];
    lcd_buffer[j++] = font[i++];
    lcd_buffer[j++] = font[i++];
    lcd_buffer[j] = 0;
    x += 6;
  }
  while (x < LCDWIDTH) {
    int j = LCDHEADER + x + y * (LCDHEADER + LCDWIDTH);
    lcd_buffer[j] = 0;
    x++;
  }
}

void LCD_drawStringInvN(int x, int y, const char *str, int N) {
  (void)N;
  if ((y < 0) || (y >= (LCDROWS))|| (x<0))return;
  unsigned char c;
  lcd_buffer[LCDHEADER + x + y * (LCDHEADER + LCDWIDTH)] = 0xFF;
  x++;
  while ((c = *str++)) {
    if (x >= (LCDWIDTH - 5))
      break;
    int i = c * 5;
    int j = LCDHEADER + x + y * (LCDHEADER + LCDWIDTH);
    lcd_buffer[j++] = ~font[i++];
    lcd_buffer[j++] = ~font[i++];
    lcd_buffer[j++] = ~font[i++];
    lcd_buffer[j++] = ~font[i++];
    lcd_buffer[j++] = ~font[i++];
    lcd_buffer[j] = 0xFF;
    x += 6;
  }
  while (x < LCDWIDTH) {
    int j = LCDHEADER + x + y * (LCDHEADER + LCDWIDTH);
    lcd_buffer[j] = 0xFF;
    x++;
  }
}

void LCD_drawIBAR(int x, int y, int v, int N) {
  if ((y < 0) || (y >= (LCDHEIGHT / 8)) || (x < 0))
    return;
  int j = LCDHEADER + x + (y * LCDWIDTH);
  int k = 1;
  int i;
  if (v > 0) {
    for (i = 0; i < 30; i++) {
      x++;
      lcd_buffer[j++] = (v > k) << 1;
      k = k << 1;
    }
  }
  else {
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

void LCD_drawIBARadd(int x, int y, int v) {
  if ((y < 0) || (y >= (LCDHEIGHT)) || (x < 0))
    return;
  int j = LCDHEADER + x + (y * LCDWIDTH);
  int b = 1 << (y & 0x07);
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

