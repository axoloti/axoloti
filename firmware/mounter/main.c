// adapted from :
/*
    ChibiOS/HAL - Copyright (C) 2016 Uladzimir Pylinsky aka barthess

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

#include <stdio.h>
#include <string.h>

#include "ch.h"
#include "hal.h"

#include "usbcfg.h"
#include "hal_usb_msd.h"
#include "axoloti_board.h"
#include "fourcc.h"

static uint8_t blkbuf[512];



#if 0
/* Turns on a LED when there is I/O activity on the USB port */
static void usbActivity(bool active)
{
    if (active)
        palSetPad(LED1_PORT, LED1_PIN);
    else
        palClearPad(LED1_PORT, LED1_PIN);
}
#endif

BaseSequentialStream *GlobalDebugChannel;

static const SerialConfig sercfg = {
    115200,
    0,
    0,
    0
};

void halt(const char *reason) {
	  if (CoreDebug->DHCSR&CoreDebug_DHCSR_C_DEBUGEN_Msk) {
		  // debugger connected, software breakpoint
		  __asm("BKPT 255");
	  }
	  (void)reason;
	  NVIC_SystemReset();
}

int main(void)
{
    /* system & hardware initialization */
    halInit();
    // float usb inputs, hope the host notices detach...
    palSetPadMode(GPIOA, 11, PAL_MODE_INPUT);
    palSetPadMode(GPIOA, 12, PAL_MODE_INPUT);
    // setup LEDs, red+green on
    palSetPadMode(LED1_PORT, LED1_PIN, PAL_MODE_OUTPUT_PUSHPULL);
    palSetPadMode(LED2_PORT, LED2_PIN, PAL_MODE_OUTPUT_PUSHPULL);
    palClearPad(LED1_PORT,LED1_PIN);
    palClearPad(LED2_PORT,LED2_PIN);

#if (CH_DBG_SYSTEM_STATE_CHECK == TRUE)
  // avoid trapping into _dbg_check_enable
  ch.dbg.isr_cnt = 0;
  ch.dbg.lock_cnt = 0;
#endif
    chSysInit();

    sdStart(&SD2, &sercfg);
    // SD2 for serial debug output
      palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(7) | PAL_MODE_INPUT); // RX
      palSetPadMode(GPIOA, 2, PAL_MODE_OUTPUT_PUSHPULL); // TX
      palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(7)); // TX
    GlobalDebugChannel = (BaseSequentialStream *)&SD2;

    palSetPadMode(GPIOA, 11, PAL_MODE_ALTERNATE(10));
    palSetPadMode(GPIOA, 12, PAL_MODE_ALTERNATE(10));

    palSetPadMode(GPIOC, 8, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 9, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 10, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 11, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOC, 12, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    palSetPadMode(GPIOD, 2, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
    chThdSleepMilliseconds(50);

    /* initialize the SD card */
    sdcStart(&SDCD1, NULL);
    bool ret = sdcConnect(&SDCD1);
    if (ret == HAL_FAILED) {
        NVIC_SystemReset();
    }
    /* turn off green LED, turn on red LED */
    palClearPad(LED1_PORT, LED1_PIN);
    palSetPad(LED2_PORT, LED2_PIN);


  /* start the USB driver */
  usbDisconnectBus(&USBD1);
  chThdSleepMilliseconds(1500);
  usbStart(&USBD1, &usbcfg);

  /*
   * start mass storage
   */
  msdObjectInit(&USBMSD1);
  msdStart(&USBMSD1, &USBD1, (BaseBlockDevice*)&SDCD1, blkbuf, NULL, NULL);
  /*
   *
   */
  usbConnectBus(&USBD1);

  /*
   * Normal main() thread activity, in this demo it does nothing except
   * sleeping in a loop.
   */
  while (true) {
    chThdSleepMilliseconds(100);
    // To check/improve: do a system reset when card is unmounted by host...
  }

  msdStop(&USBMSD1);
}

void _crt0_entry(void);

void getInstanceSize(void) {
  _crt0_entry();
}
