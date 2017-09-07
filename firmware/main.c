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

#include "axoloti_defines.h"


#include "ch.h"
#include "hal.h"
#include "sdram.h"
#include "chprintf.h"
#include "shell.h"
#include "string.h"
#include <stdio.h>
#include "codec.h"
#include "ui.h"
#include "midi.h"
#include "midi_usb.h"
#include "sdcard.h"
#include "patch.h"
#include "pconnection.h"
#include "axoloti_control.h"
#include "axoloti_math.h"
#include "axoloti_board.h"
#include "exceptions.h"
#include "watchdog.h"
#include "chprintf.h"
#include "usbcfg.h"
#include "usbh.h"
#include "sysmon.h"
#include "spilink.h"

/*===========================================================================*/
/* Initialization and main thread.                                           */
/*===========================================================================*/

#define ENABLE_SERIAL_DEBUG 1

int main(void) {
  // remap SRAM1 to 0x00000000
  // VTOR is already pointing to FLASH (in chconf.h)
  SYSCFG->MEMRMP |= 0x03;

  halInit();
  chSysInit();
  pThreadSpilink = 0;

  sdcard_init();
  sysmon_init();

#if ENABLE_SERIAL_DEBUG
// SD2 for serial debug output
  palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(7) | PAL_MODE_INPUT); // RX
  palSetPadMode(GPIOA, 2, PAL_MODE_OUTPUT_PUSHPULL); // TX
  palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(7)); // TX
// 115200 baud
  static const SerialConfig sd2Cfg = {115200,
        0, 0, 0};
  sdStart(&SD2, &sd2Cfg);
  chprintf((BaseSequentialStream * )&SD2,"Hello world!\r\n");
#endif

  exception_init();

  InitPatch0();

  InitPConnection();
  midi_usb_init();

  // display SPI CS?
  palSetPadMode(GPIOC, 1, PAL_MODE_OUTPUT_PUSHPULL);
  palSetPad(GPIOC, 1);

  chThdSleepMilliseconds(10);

  palSetPadMode(SW2_PORT, SW2_PIN, PAL_MODE_INPUT_PULLDOWN);

  axoloti_board_init();

// connect PB10 to ground to enable slave mode
  bool_t is_master = palReadPad(GPIOB, GPIOB_PIN10);
  start_dsp_thread();
  codec_init(is_master);
  adc_init();
  axoloti_math_init();
  midi_init();

  if (!palReadPad(SW2_PORT, SW2_PIN)) { // button S2 not pressed
//    watchdog_init();
    chThdSleepMilliseconds(1);
  }

  axoloti_control_init();
  spilink_init(is_master);
  ui_init();

  configSDRAM();
  //memTest();

  MY_USBH_Init();

  sdcard_attemptMountIfUnmounted();

  if (!exception_check()) {
    // only try booting a patch when no exception is to be reported
	// TODO: maybe only skip startup patch when exception was caused by startup patch
    // and button S2 is not pressed

    if (fs_ready) {
      LoadPatchStartSD();
    }
    // if no patch booting or running yet
    // try loading from flash
    if (patchStatus == STOPPED) {
        LoadPatchStartFlash();
    }
  }

	while (1) {
		usbhMainLoop(&USBHD2);
		chThdSleepMilliseconds(1000);
	}
}

void HAL_Delay(unsigned int n) {
  chThdSleepMilliseconds(n);
}
