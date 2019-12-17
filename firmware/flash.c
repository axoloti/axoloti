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
#include "flash.h"
#include "axoloti_board.h"


static __attribute__ ((section (".data"))) int flash_WaitForLastOperation(void) {
  while (FLASH->SR == FLASH_SR_BSY) {
  }
  return FLASH->SR;
}

int __attribute__ ((section (".data"))) flash_Erase_sector(int sector) {
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
  return 0;
}

int __attribute__ ((section (".data"))) flash_ProgramWord(uint32_t Address, uint32_t Data) {
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

  /* Return the Program Status */
  return status;
}

void __attribute__ ((section (".data"))) flash_unlock(void) {
  // unlock sequence
  FLASH->KEYR = 0x45670123;
  FLASH->KEYR = 0xCDEF89AB;
}

void __attribute__ ((section (".data"))) flash_lock(void) {
  FLASH->CR |= FLASH_CR_LOCK;
}

static __attribute__ ((section (".data")))
    void setLed(void) {
  palSetPad(LED2_PORT, LED2_PIN);
}

static __attribute__ ((section (".data")))
    void clearLed(void) {
  palClearPad(LED2_PORT, LED2_PIN);
}

__attribute__ ((section (".data"))) int flash_write(uint32_t pdest, uint32_t psrc, uint32_t psize) {
  if (pdest == FLASH_PATCH_ADDR) {
    port_disable();
    flash_unlock();
    int i;
    for (i = 8; i < 12; i++) {
      flash_Erase_sector(i);
      if (i&1) {
        setLed();
      } else {
        clearLed();
      }
    }
  } else if (pdest == FLASH_BASE_ADDR) {
    port_disable();
    flash_unlock();
    int i;
    for (i = 0; i < 12; i++) {
      flash_Erase_sector(i);
      if (i&1) {
        setLed();
      } else {
        clearLed();
      }
    }
  } else {
    return -1;
  }
  int src_addr = psrc;
  int flash_addr = pdest;
  int c;
  for (c = 0; c < psize;) {
    flash_ProgramWord(flash_addr, *(int32_t *)src_addr);
    src_addr += 4;
    flash_addr += 4;
    c += 4;
    if (c & 0x4000) {
      setLed();
    } else {
      clearLed();
    }
  }
  flash_lock();
  // verify
  src_addr = psrc;
  flash_addr = pdest;
  int err = 0;
  for (c = 0; c < psize;) {
    if (*(int32_t *)flash_addr != *(int32_t *)src_addr)
       err++;
    src_addr += 4;
    flash_addr += 4;
    c += 4;
  }
  if (pdest == FLASH_BASE_ADDR) {
    // NVIC_SystemReset();
    // .. code is copied to avoid "relocation truncated to fit: R_ARM_THM_CALL against symbol..."
    // error during LTO linking
    __DSB();
    SCB->AIRCR  = (uint32_t)((0x5FAUL << SCB_AIRCR_VECTKEY_Pos)    |
                             (SCB->AIRCR & SCB_AIRCR_PRIGROUP_Msk) |
                              SCB_AIRCR_SYSRESETREQ_Msk    );
    __DSB();
    for(;;)
    {
      __NOP();
    }
  }
  port_enable();
  return err;
}
