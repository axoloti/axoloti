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
#include "watchdog.h"


__attribute__ ((section (".data"))) int flash_WaitForLastOperation(void) {
  while (FLASH->SR == FLASH_SR_BSY) {
    WWDG->CR = WWDG_CR_T;
  }
  return FLASH->SR;
}

__attribute__ ((section (".data"))) void flash_Erase_sector1(int sector) {
  // assume VDD>2.7V
  FLASH->CR &= ~FLASH_CR_PSIZE;
  FLASH->CR |= FLASH_CR_PSIZE_1;
  FLASH->CR &= ~FLASH_CR_SNB;
  FLASH->CR |= FLASH_CR_SER | (sector << 3);
  FLASH->CR |= FLASH_CR_STRT;
  flash_WaitForLastOperation();

  FLASH->CR &= (~FLASH_CR_SER);
  FLASH->CR &= ~FLASH_CR_SER;
  flash_WaitForLastOperation();
}

int flash_Erase_sector(int sector) {
  // interrupts would cause flash execution, stall
  // and cause watchdog trigger
  chSysLock();
  flash_Erase_sector1(sector);
  chSysUnlock();
  return 0;
}

int flash_ProgramWord(uint32_t Address, uint32_t Data) {
  int status;

  flash_WaitForLastOperation();

  /* if the previous operation is completed, proceed to program the new data */
  FLASH->CR &= ~FLASH_CR_PSIZE;
  FLASH->CR |= FLASH_CR_PSIZE_1;
  FLASH->CR |= FLASH_CR_PG;

  *(__IO uint32_t*)Address = Data;

  /* Wait for last operation to be completed */
  status = flash_WaitForLastOperation();

  /* if the program operation is completed, disable the PG Bit */
  FLASH->CR &= (~FLASH_CR_PG);

  watchdog_feed();

  /* Return the Program Status */
  return status;
}

void flash_unlock(void) {
  // unlock sequence
  FLASH->KEYR = 0x45670123;
  FLASH->KEYR = 0xCDEF89AB;
}
