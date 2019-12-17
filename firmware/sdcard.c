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
#include "chprintf.h"
#include "ff.h"
#include "patch.h"
#include "sdcard.h"
#include <string.h>
#include "chprintf.h"
#include "exceptions.h"

/*===========================================================================*/
/* SDCard                                                                    */
/*===========================================================================*/

/* FS object.*/
FATFS SDC_FS;

/* FS mounted and ready.*/
bool fs_ready = FALSE;

/*
 * Card insertion event.
 */
static void InsertHandler(eventid_t id) {
  FRESULT err;

  (void)id;
  /*
   * On insertion SDC initialization and FS mount.
   */
  if (fs_ready) {
    sdcDisconnect(&SDCD1);
    fs_ready = FALSE;
  }

  if (sdcConnect(&SDCD1))
    return;

  err = f_mount(&SDC_FS, "/", 1);
  if (err != FR_OK) {
    sdcDisconnect(&SDCD1);
    return;
  }
  fs_ready = TRUE;
}

/*
 * Card removal event.
 */
static void RemoveHandler(eventid_t id) {

  (void)id;
  sdcDisconnect(&SDCD1);
  fs_ready = FALSE;
}

void sdcard_init(void) {
  /*
   static const evhandler_t evhndl[] = {
   InsertHandler,
   RemoveHandler
   };
   struct EventListener el0, el1;
   */
  palSetPadMode(GPIOC, 8, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 9, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 10, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 11, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOC, 12, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  palSetPadMode(GPIOD, 2, PAL_MODE_ALTERNATE(12) | PAL_STM32_OSPEED_HIGHEST);
  chThdSleepMicroseconds(1);
  sdcStart(&SDCD1, NULL);
  chThdSleepMilliseconds(50);

  InsertHandler(0);

  FRESULT err;
  uint32_t clusters;
  FATFS *fsp;

  err = f_getfree("/", &clusters, &fsp);
  int retries = 3;
  while (err != FR_OK) {
    InsertHandler(0);
    chThdSleepMilliseconds(20);
    err = f_getfree("/", &clusters, &fsp);
    chThdSleepMilliseconds(50);
    retries--;
    if (!retries)
      break;
  }
}

void sdcard_attemptMountIfUnmounted() {
  if (fs_ready)
    return;
  InsertHandler(0);
}

void sdcard_unmount(void){
  RemoveHandler(0);
}
