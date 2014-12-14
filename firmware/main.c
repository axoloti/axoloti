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
#include <stdio.h>
#include "hal.h"
#include "chprintf.h"
#include "shell.h"
#include "string.h"

#include "codec.h"
#include "ui.h"
#include "midi.h"
#include "sdcard.h"
#include "patch.h"
#include "pconnection.h"
#include "axoloti_control.h"
#include "axoloti_board.h"
#include "axoloti_math.h"
/*===========================================================================*/
/* Initialization and main thread.                                           */
/*===========================================================================*/
// #define ENABLE_USB_HOST
#ifdef ENABLE_USB_HOST
#if (BOARD_AXOLOTI_V03)
#error conflicting pins: USB_OTG_HS and I2S
#endif
extern void MY_USBH_Init(void);
#endif

#if (BOARD_STM32F4DISCOVERY)
void ToggleGreen(void) {
  palSetPadMode(GPIOD, 12, PAL_MODE_OUTPUT_PUSHPULL);
  palTogglePad(GPIOD, 12);
}
void ToggleOrange(void) {
  palSetPadMode(GPIOD, 13, PAL_MODE_OUTPUT_PUSHPULL);
  palTogglePad(GPIOD, 13);
}
void ToggleRed(void) {
  palSetPadMode(GPIOD, 14, PAL_MODE_OUTPUT_PUSHPULL);
  palTogglePad(GPIOD, 14);
}
void ToggleBlue(void) {
  palSetPadMode(GPIOD, 15, PAL_MODE_OUTPUT_PUSHPULL);
  palTogglePad(GPIOD, 15);
}
#endif

int main(void) {
  // copy vector table to SRAM1!
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wnonnull"
  memcpy((char *)0x20000000, (const char)0x00000000, 0x200);
#pragma GCC diagnostic pop
  // remap SRAM1 to 0x00000000
  SYSCFG ->MEMRMP |= 0x03;

  halInit();
  chSysInit();

  InitPatch0();

  InitPConnection();

  InitPWM();

  // display SPI CS?
  palSetPadMode(GPIOC, 1, PAL_MODE_OUTPUT_PUSHPULL);
  palSetPad(GPIOC, 1);

  chThdSleepMilliseconds(10);

  sdcardInit();

  palSetPadMode(GPIOB, 2, PAL_MODE_INPUT_PULLDOWN);

  axoloti_board_init();
  axoloti_math_init();
  midi_init();
  codec_init();

#if (BOARD_AXOLOTI_V03)
  axoloti_control_init();
#endif
  ui_init();
  StartLoadPatchTread();

#if (BOARD_AXOLOTI_V03)
  if (!palReadPad(GPIOB, 2)) // button S2 not pressed
    SDLoadPatch("0:start.bin");
#endif

  // if no patch booting or running yet
  // try loading from flash
  if (patchStatus) {
    // patch in flash sector 11
    memcpy((uint8_t *)PATCHMAINLOC, 0x080E0000, 0xE000);
    if ((*(uint32_t *)PATCHMAINLOC != 0xFFFFFFFF)
        && (*(uint32_t *)PATCHMAINLOC != 0)) {
      StartPatch();
    }
  }

#ifdef ENABLE_USB_HOST
// SD2 for serial debug output
  palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(7)|PAL_MODE_INPUT);// RX
  palSetPadMode(GPIOA, 2, PAL_MODE_OUTPUT_PUSHPULL);// TX
  palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(7));// TX
// 115200 baud
  static const SerialConfig sd2Cfg = {115200,
    0, 0, 0};
  sdStart(&SD2, &sd2Cfg);

  MY_USBH_Init();
#endif
  while (1) {
    chThdSleepMilliseconds(1000);
  }
}

void HAL_Delay(unsigned int n) {
  chThdSleepMilliseconds(n);
}

void _sbrk(void) {
  while (1) {
  }
}
