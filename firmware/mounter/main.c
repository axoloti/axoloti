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
  msdStart(&USBMSD1, &USBD1, (BaseBlockDevice*)&SDCD1, blkbuf, NULL);
  /*
   *
   */
  usbConnectBus(&USBD1);

  /*
   * Normal main() thread activity, in this demo it does nothing except
   * sleeping in a loop and check the button state.
   */
  while (true) {
    chThdSleepMilliseconds(100);
    // Todo: reset when card is unmounted by host...
    /*
    if (USBMSD1.state == USB_MSD_STOP) {
        chThdSleepMilliseconds(100);
        NVIC_SystemReset();
    }
    if (USBMSD1.scsi_target.state == SCSI_TRGT_STOP) {
        chThdSleepMilliseconds(1);
        NVIC_SystemReset();
    }
    */
  }

  msdStop(&USBMSD1);
}

extern void Reset_Handler(void);

__attribute__ ((section (".entry_section"))) void entryfn (int fwid) {
	(void)fwid;
	SCB->VTOR = CORTEX_VTOR_INIT;
	Reset_Handler();
}

