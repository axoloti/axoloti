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
#include "string.h"
#include "codec.h"
#include "ui.h"
#include "midi_usb.h"
#include "sdcard.h"
#include "patch.h"
#include "patch_impl.h"
#include "pconnection.h"
#include "axoloti_control.h"
#include "axoloti_math.h"
#include "axoloti_board.h"
#include "axoloti_memory_impl.h"
#include "exceptions.h"
#include "usbcfg.h"
#include "usbh.h"
#include "sysmon.h"
#include "spilink.h"
#include "dbg_stream.h"
#include "sys/fcntl.h"

/*===========================================================================*/
/* Initialization and main thread.                                           */
/*===========================================================================*/

int main(void) {
  // remap SRAM1 to 0x00000000
  // VTOR is already pointing to FLASH (in chconf.h)
  SYSCFG->MEMRMP |= 0x03;

  halInit();
  chSysInit();

  configSDRAM();
  axoloti_mem_init();

  pThreadSpilink = 0;

  sdcard_init();
  sysmon_init();

  dbg_stream_init();
//  chprintf(dbg_stream,"Hello world!\r\n");

  // semihosting test
//  int fid = _open("test.txt", O_RDONLY|O_CREAT);
//  _write(fid,"xxxx",3);
//  _close(fid);


  exception_init();

  InitPConnection();
  midi_usb_init();

  // display SPI CS?
  palSetPadMode(GPIOC, 1, PAL_MODE_OUTPUT_PUSHPULL);
  palSetPad(GPIOC, 1);

  chThdSleepMilliseconds(200);

  palSetPadMode(SW2_PORT, SW2_PIN, PAL_MODE_INPUT_PULLDOWN);

  axoloti_board_init();

// connect PB10 to ground to enable slave mode
  bool is_master = palReadPad(GPIOB, GPIOB_PIN10);
  adc_init();
  axoloti_math_init();
  extern void midi_init(); // TODO: cleanup
  midi_init();

  if (!palReadPad(SW2_PORT, SW2_PIN)) { // button S2 not pressed
    chThdSleepMilliseconds(1);
  }

  axoloti_control_init();
  spilink_init(is_master);
  ui_init();

  //memTest();

  MY_USBH_Init();

  sdcard_attemptMountIfUnmounted();

  start_dsp_thread();
  codec_init(is_master);

  if (!exception_check() && !palReadPad(SW2_PORT, SW2_PIN))  {
    // only try booting a patch when no exception is to be reported
    // and button S2 is not pressed
    patch_t * patch = 0;
    if (fs_ready) {
      patch = patch_loadStartSD(0);
    }
    // if no patch booting or running yet
    // try loading from flash
    if (!patch) {
      // load patch in flash
      patch = patch_load("@08080000:flash",0);
    }
  }
	while (1) {
		chThdSleepMilliseconds(1000);
	}
}

void HAL_Delay(unsigned int n) {
  chThdSleepMilliseconds(n);
}
