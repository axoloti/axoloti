/**
 * Copyright (C) 2015 Johannes Taelman
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

#include "crc32.h"

#include "ch.h"
#include "hal.h"
#include "exceptions.h"

uint32_t revbit(uint32_t data) {
  uint32_t result;
  __ASM
  volatile ("rbit %0, %1" : "=r" (result) : "r" (data));
  return result;
}

uint32_t CalcCRC32(uint8_t *buffer, uint32_t size) {
  uint32_t i, j;
  uint32_t ui32;

  RCC->AHB1ENR |= RCC_AHB1ENR_CRCEN;
  CRC->CR = 1;
  asm("NOP");
  asm("NOP");
  asm("NOP");
  //delay for hardware ready

  i = size >> 2;

  while (i--) {
    ui32 = *((uint32_t *)buffer);
    buffer += 4;
    ui32 = revbit(ui32); //reverse the bit order of input data
    CRC->DR = ui32;
    if ((i && 0xFFF) == 0)
      watchdog_feed();
  }
  ui32 = CRC->DR;

  ui32 = revbit(ui32); //reverse the bit order of output data
  i = size & 3;
  while (i--) {
    ui32 ^= (u32) * buffer++;

    for (j = 0; j < 8; j++)
      if (ui32 & 1)
        ui32 = (ui32 >> 1) ^ 0xEDB88320;
      else
        ui32 >>= 1;
  }
  ui32 ^= 0xffffffff; //xor with 0xffffffff
  return ui32; //now the output is compatible with windows/winzip/winrar
}
